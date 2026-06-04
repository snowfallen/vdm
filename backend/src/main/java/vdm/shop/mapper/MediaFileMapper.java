package vdm.shop.mapper;

import org.mapstruct.Mapper;
import vdm.shop.dto.media.MediaFileResponseDto;
import vdm.shop.model.MediaFile;

@Mapper(componentModel = "spring")
public interface MediaFileMapper {
    MediaFileResponseDto toDto(MediaFile mediaFile);
}
