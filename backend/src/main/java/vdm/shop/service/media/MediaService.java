package vdm.shop.service.media;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import vdm.shop.dto.media.MediaFileResponseDto;
import vdm.shop.dto.media.MediaSettingResponseDto;

public interface MediaService {

    // ─── Settings ────────────────────────────────────────────────

    MediaSettingResponseDto getAllSettings();

    void updateSettings(Map<String, String> updates);

    // ─── Files ───────────────────────────────────────────────────

    List<MediaFileResponseDto> getFilesByKey(String fileKey);

    MediaFileResponseDto uploadFile(String fileKey, MultipartFile file,
                                    String title) throws IOException;

    void deleteFile(Long id);

    void reorderFiles(String fileKey, List<Long> orderedIds);

    MediaFileResponseDto updateFileTitle(Long id, String title);
}
