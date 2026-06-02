package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.productgroup.ProductGroupRequestDto;
import vdm.shop.dto.productgroup.ProductGroupResponseDto;
import vdm.shop.model.ProductGroup;

@Mapper(config = MapperConfig.class)
public interface ProductGroupMapper {
    @Mapping(target = "subCategory", ignore = true)
    ProductGroup toModel(ProductGroupRequestDto requestDto);

    @Mapping(target = "subCategoryId", source = "subCategory.id")
    ProductGroupResponseDto toDto(ProductGroup productGroup);

    @Mapping(target = "subCategoryId", source = "subCategory.id")
    List<ProductGroupResponseDto> toDtoList(List<ProductGroup> productGroups);

    default Page<ProductGroupResponseDto> toDtoPage(Page<ProductGroup> productGroups) {
        List<ProductGroupResponseDto> dtoList = productGroups.map(this::toDto).getContent();
        return new PageImpl<>(dtoList, productGroups.getPageable(),
                productGroups.getTotalElements());
    }
}
