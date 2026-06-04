package vdm.shop.dto.media;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaSettingRequestDto {
    private Map<String, String> settings;
}
