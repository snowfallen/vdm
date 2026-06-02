package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.attributeoption.AttributeOptionRequestDto;
import vdm.shop.dto.attributeoption.AttributeOptionResponseDto;
import vdm.shop.model.AttributeOption;

@Mapper(config = MapperConfig.class)
public interface AttributeOptionMapper {
    @Mapping(target = "attribute", ignore = true)
    AttributeOption toModel(AttributeOptionRequestDto requestDto);

    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    AttributeOptionResponseDto toDto(AttributeOption option);

    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    List<AttributeOptionResponseDto> toDtoList(List<AttributeOption> options);
}
