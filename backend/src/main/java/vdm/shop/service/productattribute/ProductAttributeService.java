package vdm.shop.service.productattribute;

import java.util.List;
import vdm.shop.dto.productattribute.ProductAttributeRequestDto;
import vdm.shop.dto.productattribute.ProductAttributeResponseDto;

public interface ProductAttributeService {
    ProductAttributeResponseDto create(ProductAttributeRequestDto requestDto);

    List<ProductAttributeResponseDto> getAllByProductId(Long productId);

    ProductAttributeResponseDto getById(Long id);

    ProductAttributeResponseDto update(Long id, ProductAttributeRequestDto requestDto);

    ProductAttributeResponseDto delete(Long id);
}
