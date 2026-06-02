package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.attribute.AttributeRequestDto;
import vdm.shop.dto.attribute.AttributeResponseDto;
import vdm.shop.model.Attribute;

@Mapper(config = MapperConfig.class)
public interface AttributeMapper {
    @Mapping(target = "unit", ignore = true)
    Attribute toModel(AttributeRequestDto requestDto);

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitSymbol", source = "unit.symbol")
    AttributeResponseDto toDto(Attribute attribute);

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitSymbol", source = "unit.symbol")
    List<AttributeResponseDto> toDtoList(List<Attribute> attributes);

    default Page<AttributeResponseDto> toDtoPage(Page<Attribute> attributes) {
        List<AttributeResponseDto> dtoList = attributes.map(this::toDto).getContent();
        return new PageImpl<>(dtoList, attributes.getPageable(), attributes.getTotalElements());
    }
}
