package vdm.shop.service.subcategory;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.subcategory.SubCategoryRequestDto;
import vdm.shop.dto.subcategory.SubCategoryResponseDto;

public interface SubCategoryService {
    SubCategoryResponseDto create(SubCategoryRequestDto requestDto);

    Page<SubCategoryResponseDto> getAll(Pageable pageable);

    List<SubCategoryResponseDto> getAllByCategoryId(Long categoryId);

    SubCategoryResponseDto getById(Long id);

    SubCategoryResponseDto update(Long id, SubCategoryRequestDto requestDto);

    SubCategoryResponseDto delete(Long id);
}
