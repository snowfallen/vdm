package vdm.shop.service.productgroup;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.productgroup.ProductGroupRequestDto;
import vdm.shop.dto.productgroup.ProductGroupResponseDto;

public interface ProductGroupService {
    ProductGroupResponseDto create(ProductGroupRequestDto requestDto);

    Page<ProductGroupResponseDto> getAll(Pageable pageable);

    List<ProductGroupResponseDto> getAllBySubCategoryId(Long subCategoryId);

    ProductGroupResponseDto getById(Long id);

    ProductGroupResponseDto update(Long id, ProductGroupRequestDto requestDto);

    ProductGroupResponseDto delete(Long id);
}
