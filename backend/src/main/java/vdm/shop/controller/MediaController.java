package vdm.shop.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vdm.shop.dto.media.MediaFileReorderRequestDto;
import vdm.shop.dto.media.MediaFileResponseDto;
import vdm.shop.dto.media.MediaFileUpdateTitleRequestDto;
import vdm.shop.dto.media.MediaSettingRequestDto;
import vdm.shop.dto.media.MediaSettingResponseDto;
import vdm.shop.service.media.MediaService;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/settings")
    public ResponseEntity<MediaSettingResponseDto> getSettings() {
        return ResponseEntity.ok(mediaService.getAllSettings());
    }

    @GetMapping("/files/{fileKey}")
    public ResponseEntity<List<MediaFileResponseDto>> getFiles(
            @PathVariable String fileKey) {
        return ResponseEntity.ok(mediaService.getFilesByKey(fileKey));
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateSettings(
            @RequestBody MediaSettingRequestDto requestDto) {
        mediaService.updateSettings(requestDto.getSettings());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload/{fileKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MediaFileResponseDto> uploadFile(
            @PathVariable String fileKey,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title)
            throws IOException {
        return ResponseEntity.ok(mediaService.uploadFile(fileKey, file, title));
    }

    @DeleteMapping("/files/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        mediaService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/files/reorder/{fileKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reorderFiles(
            @PathVariable String fileKey,
            @RequestBody MediaFileReorderRequestDto requestDto) {
        mediaService.reorderFiles(fileKey, requestDto.getOrderedIds());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/files/{id}/title")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MediaFileResponseDto> updateTitle(
            @PathVariable Long id,
            @RequestBody MediaFileUpdateTitleRequestDto requestDto) {
        return ResponseEntity.ok(
                mediaService.updateFileTitle(id, requestDto.getTitle()));
    }
}
