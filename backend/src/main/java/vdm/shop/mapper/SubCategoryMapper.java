package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.subcategory.SubCategoryRequestDto;
import vdm.shop.dto.subcategory.SubCategoryResponseDto;
import vdm.shop.model.SubCategory;

@Mapper(config = MapperConfig.class)
public interface SubCategoryMapper {
    @Mapping(target = "category", ignore = true)
    SubCategory toModel(SubCategoryRequestDto requestDto);

    @Mapping(target = "categoryId", source = "category.id")
    SubCategoryResponseDto toDto(SubCategory subCategory);

    @Mapping(target = "categoryId", source = "category.id")
    List<SubCategoryResponseDto> toDtoList(List<SubCategory> subCategories);

    default Page<SubCategoryResponseDto> toDtoPage(Page<SubCategory> subCategories) {
        List<SubCategoryResponseDto> dtoList = subCategories.map(this::toDto).getContent();
        return new PageImpl<>(dtoList, subCategories.getPageable(),
                subCategories.getTotalElements());
    }
}
