package vdm.shop.service.category;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.category.CategoryRequestDto;
import vdm.shop.dto.category.CategoryResponseDto;

public interface CategoryService {
    CategoryResponseDto create(CategoryRequestDto requestDto);

    Page<CategoryResponseDto> getAll(Pageable pageable);

    List<CategoryResponseDto> getAllList();

    CategoryResponseDto getById(Long id);

    CategoryResponseDto update(Long id, CategoryRequestDto requestDto);

    CategoryResponseDto delete(Long id);
}
