package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.category.CategoryRequestDto;
import vdm.shop.dto.category.CategoryResponseDto;
import vdm.shop.model.Category;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    Category toModel(CategoryRequestDto requestDto);

    CategoryResponseDto toDto(Category category);

    List<CategoryResponseDto> toDtoList(List<Category> categories);

    default Page<CategoryResponseDto> toDtoPage(Page<Category> categories) {
        List<CategoryResponseDto> dtoList = categories.map(this::toDto).getContent();
        return new PageImpl<>(dtoList, categories.getPageable(), categories.getTotalElements());
    }
}
