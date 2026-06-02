package vdm.shop.service.product;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.product.ProductRequestDto;
import vdm.shop.dto.product.ProductResponseDto;

public interface ProductService {
    ProductResponseDto create(ProductRequestDto requestDto);

    Page<ProductResponseDto> getAll(Pageable pageable);

    List<ProductResponseDto> getAllByProductGroupId(Long productGroupId);

    ProductResponseDto getById(Long id);

    ProductResponseDto update(Long id, ProductRequestDto requestDto);

    ProductResponseDto delete(Long id);
}
