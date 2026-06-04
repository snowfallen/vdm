package vdm.shop.dto.media;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaFileReorderRequestDto {
    private List<Long> orderedIds;
}
