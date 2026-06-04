package vdm.shop.service.media.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vdm.shop.dto.media.MediaFileResponseDto;
import vdm.shop.dto.media.MediaSettingResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.MediaFileMapper;
import vdm.shop.model.MediaFile;
import vdm.shop.model.MediaSetting;
import vdm.shop.repository.media.MediaFileRepository;
import vdm.shop.repository.media.MediaSettingRepository;
import vdm.shop.service.media.MediaService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    private final MediaFileRepository    mediaFileRepository;
    private final MediaSettingRepository mediaSettingRepository;
    private final MediaFileMapper        mediaFileMapper;
    private final MinioClient            minioClient;

    @Value("${minio.bucket:vdm-media}")
    private String bucket;

    @Value("${app.public-url:http://localhost:9000}")
    private String publicUrl;

    // ─── Settings ────────────────────────────────────────────────

    @Override
    public MediaSettingResponseDto getAllSettings() {
        Map<String, String> map = mediaSettingRepository.findAll().stream()
                .collect(Collectors.toMap(
                        MediaSetting::getSettingKey,
                        s -> s.getSettingValue() != null ? s.getSettingValue() : "",
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        return MediaSettingResponseDto.builder()
                .settings(map)
                .build();
    }

    @Override
    @Transactional
    public void updateSettings(Map<String, String> updates) {
        updates.forEach((key, value) -> {
            MediaSetting setting = mediaSettingRepository.findBySettingKey(key)
                    .orElseGet(() -> MediaSetting.builder()
                            .settingKey(key)
                            .build());
            setting.setSettingValue(value);
            mediaSettingRepository.save(setting);
            log.info("Media setting updated: key={}", key);
        });
    }

    // ─── Files ───────────────────────────────────────────────────

    @Override
    public List<MediaFileResponseDto> getFilesByKey(String fileKey) {
        return mediaFileRepository.findByFileKeyOrderBySortOrderAsc(fileKey)
                .stream()
                .map(mediaFileMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public MediaFileResponseDto uploadFile(String fileKey, MultipartFile file,
                                           String title) throws IOException {
        log.info("uploadFile: fileKey={}, originalName={}", fileKey, file.getOriginalFilename());

        byte[] originalBytes = file.getBytes();

        // Визначаємо формат залежно від прозорості
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalBytes));
        boolean hasAlpha = img != null && img.getColorModel().hasAlpha();
        String ext         = hasAlpha ? ".png"       : ".jpg";
        String contentType = hasAlpha ? "image/png"  : "image/jpeg";

        byte[] bytes = compress(originalBytes, hasAlpha, 0.88f);

        String objectName = fileKey + "/" + UUID.randomUUID() + ext;

        try {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                    .contentType(contentType)
                    .build());
            log.info("Uploaded to MinIO: bucket={}, object={}, type={}", bucket, objectName, contentType);
        } catch (Exception e) {
            log.error("MinIO upload failed: {}", e.getMessage(), e);
            throw new IOException("Failed to upload file to MinIO: " + e.getMessage(), e);
        }

        String publicUrl = buildPublicUrl(objectName);

        int nextOrder = mediaFileRepository.findByFileKeyOrderBySortOrderAsc(fileKey)
                .stream()
                .mapToInt(MediaFile::getSortOrder)
                .max()
                .orElse(-1) + 1;

        MediaFile saved = mediaFileRepository.save(
                MediaFile.builder()
                        .fileKey(fileKey)
                        .objectName(objectName)
                        .url(publicUrl)
                        .originalName(file.getOriginalFilename())
                        .title(title != null && !title.isBlank()
                                ? title
                                : file.getOriginalFilename())
                        .sortOrder(nextOrder)
                        .build()
        );

        log.info("MediaFile saved: id={}, fileKey={}", saved.getId(), fileKey);
        return mediaFileMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        log.info("deleteFile: id={}", id);

        MediaFile file = mediaFileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MediaFile not found: " + id));

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(file.getObjectName())
                    .build());
            log.info("Deleted from MinIO: object={}", file.getObjectName());
        } catch (Exception e) {
            log.warn("Could not delete from MinIO (id={}): {}", id, e.getMessage());
        }

        mediaFileRepository.delete(file);
    }

    @Override
    @Transactional
    public void reorderFiles(String fileKey, List<Long> orderedIds) {
        log.info("reorderFiles: fileKey={}", fileKey);
        for (int i = 0; i < orderedIds.size(); i++) {
            final int idx = i;
            mediaFileRepository.findById(orderedIds.get(i)).ifPresent(f -> {
                f.setSortOrder(idx);
                mediaFileRepository.save(f);
            });
        }
    }

    @Override
    @Transactional
    public MediaFileResponseDto updateFileTitle(Long id, String title) {
        log.info("updateFileTitle: id={}, title={}", id, title);
        MediaFile file = mediaFileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MediaFile not found: " + id));
        file.setTitle(title);
        return mediaFileMapper.toDto(mediaFileRepository.save(file));
    }

    // ─── Private helpers ─────────────────────────────────────────

    /**
     * Стискає зображення через Thumbnailator (чиста Java, без нативних бібліотек).
     * PNG з прозорістю зберігається як PNG, інші — як JPEG (якість 0–1).
     * Thumbnailator не підтримує WebP — це обмеження бібліотеки.
     * Для реального WebP на Linux AMD64 можна підключити webp-imageio.
     */
    private byte[] compress(byte[] input, boolean preserveAlpha, float quality)
            throws IOException {
        try {
            String format = preserveAlpha ? "png" : "jpeg";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(input))
                    .scale(1.0)
                    .outputFormat(format)
                    .outputQuality(quality)   // ігнорується для PNG
                    .toOutputStream(out);
            log.debug("Image compressed: format={}, {} → {} bytes",
                    format, input.length, out.size());
            return out.toByteArray();
        } catch (IOException e) {
            log.warn("Thumbnailator compression failed, using original bytes: {}", e.getMessage());
            return input;
        }
    }

    private String buildPublicUrl(String objectName) {
        return publicUrl + "/" + bucket + "/" + objectName;
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build());
            String policy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [{
                        "Effect": "Allow",
                        "Principal": { "AWS": ["*"] },
                        "Action": ["s3:GetObject"],
                        "Resource": ["arn:aws:s3:::%s/*"]
                      }]
                    }
                    """.formatted(bucket);
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucket)
                            .config(policy)
                            .build());
            log.info("Created MinIO bucket with public read policy: {}", bucket);
        }
    }
}
