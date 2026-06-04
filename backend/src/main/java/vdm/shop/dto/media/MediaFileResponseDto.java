package vdm.shop.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFileResponseDto {
    private Long id;
    private String fileKey;
    private String url;
    private String title;
    private Integer sortOrder;
    private String originalName;
}
