package vdm.shop.service.attribute;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.attribute.AttributeRequestDto;
import vdm.shop.dto.attribute.AttributeResponseDto;
import vdm.shop.dto.attribute.AttributeWithOptionsResponseDto;
import vdm.shop.dto.attribute.SubCategoryFiltersResponseDto;

public interface AttributeService {
    AttributeResponseDto create(AttributeRequestDto requestDto);

    Page<AttributeResponseDto> getAll(Pageable pageable);

    List<AttributeResponseDto> getAllList();

    AttributeResponseDto getById(Long id);

    AttributeResponseDto update(Long id, AttributeRequestDto requestDto);

    AttributeResponseDto delete(Long id);

    // Атрибут + всі його опції (для адмінки)
    AttributeWithOptionsResponseDto getWithOptions(Long id);

    // Всі фільтри для сторінки підкатегорії (з реальними значеннями товарів)
    SubCategoryFiltersResponseDto getFiltersForSubCategory(Long subCategoryId);

    SubCategoryFiltersResponseDto getFiltersForProductGroup(Long productGroupId);
}
