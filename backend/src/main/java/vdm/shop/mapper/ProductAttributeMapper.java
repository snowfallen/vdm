package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.productattribute.ProductAttributeRequestDto;
import vdm.shop.dto.productattribute.ProductAttributeResponseDto;
import vdm.shop.model.ProductAttribute;

@Mapper(config = MapperConfig.class)
public interface ProductAttributeMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    @Mapping(target = "optionValue", ignore = true)
    ProductAttribute toModel(ProductAttributeRequestDto requestDto);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    @Mapping(target = "dataType", source = "attribute.dataType")
    @Mapping(target = "unitSymbol", source = "attribute.unit.symbol")
    @Mapping(target = "optionId", source = "optionValue.id")
    @Mapping(target = "value", ignore = true)
    ProductAttributeResponseDto toDto(ProductAttribute productAttribute);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    @Mapping(target = "dataType", source = "attribute.dataType")
    @Mapping(target = "unitSymbol", source = "attribute.unit.symbol")
    @Mapping(target = "optionId", source = "optionValue.id")
    @Mapping(target = "value", ignore = true)
    List<ProductAttributeResponseDto> toDtoList(List<ProductAttribute> productAttributes);

    @AfterMapping
    default void setValue(@MappingTarget ProductAttributeResponseDto dto,
                          ProductAttribute productAttribute) {
        // optionValue має пріоритет над customValue
        if (productAttribute.getOptionValue() != null) {
            dto.setValue(productAttribute.getOptionValue().getValue());
        } else {
            dto.setValue(productAttribute.getCustomValue());
        }
    }
}
