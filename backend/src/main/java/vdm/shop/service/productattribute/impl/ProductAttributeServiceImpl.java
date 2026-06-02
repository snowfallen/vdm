package vdm.shop.service.productattribute.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vdm.shop.dto.productattribute.ProductAttributeRequestDto;
import vdm.shop.dto.productattribute.ProductAttributeResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.ProductAttributeMapper;
import vdm.shop.model.Attribute;
import vdm.shop.model.AttributeOption;
import vdm.shop.model.Product;
import vdm.shop.model.ProductAttribute;
import vdm.shop.model.enumeration.AttributeDataType;
import vdm.shop.repository.attribute.AttributeRepository;
import vdm.shop.repository.attributeoption.AttributeOptionRepository;
import vdm.shop.repository.product.ProductRepository;
import vdm.shop.repository.productattribute.ProductAttributeRepository;
import vdm.shop.service.productattribute.ProductAttributeService;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductAttributeServiceImpl implements ProductAttributeService {
    private static final String NOT_FOUND = "ProductAttribute not found by id: ";
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final ProductAttributeMapper productAttributeMapper;

    @Override
    public ProductAttributeResponseDto create(ProductAttributeRequestDto requestDto) {
        log.info("Creating productAttribute for product id: {}", requestDto.productId());
        ProductAttribute pa = buildProductAttribute(new ProductAttribute(), requestDto);
        ProductAttribute saved = productAttributeRepository.save(pa);
        log.info("ProductAttribute created with id: {}", saved.getId());
        return productAttributeMapper.toDto(saved);
    }

    @Override
    public List<ProductAttributeResponseDto> getAllByProductId(Long productId) {
        log.info("Fetching productAttributes for product id: {}", productId);
        return productAttributeMapper.toDtoList(
                productAttributeRepository.findAllByProductId(productId));
    }

    @Override
    public ProductAttributeResponseDto getById(Long id) {
        log.info("Fetching productAttribute by id: {}", id);
        return productAttributeMapper.toDto(getProductAttribute(id));
    }

    @Override
    public ProductAttributeResponseDto update(Long id, ProductAttributeRequestDto requestDto) {
        log.info("Updating productAttribute with id: {}", id);
        ProductAttribute pa = buildProductAttribute(getProductAttribute(id), requestDto);
        ProductAttribute updated = productAttributeRepository.save(pa);
        log.info("ProductAttribute with id: {} updated", id);
        return productAttributeMapper.toDto(updated);
    }

    @Override
    public ProductAttributeResponseDto delete(Long id) {
        log.info("Deleting productAttribute with id: {}", id);
        ProductAttribute pa = getProductAttribute(id);
        ProductAttributeResponseDto dto = productAttributeMapper.toDto(pa);
        productAttributeRepository.delete(pa);
        log.info("ProductAttribute with id: {} deleted", id);
        return dto;
    }

    private ProductAttribute buildProductAttribute(ProductAttribute pa,
                                                    ProductAttributeRequestDto dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found by id: " + dto.productId()));
        Attribute attribute = attributeRepository.findById(dto.attributeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Attribute not found by id: " + dto.attributeId()));

        pa.setProduct(product);
        pa.setAttribute(attribute);

        if (attribute.getDataType() == AttributeDataType.DICT) {
            if (dto.optionId() == null) {
                throw new IllegalArgumentException(
                        "optionId required for DICT attribute id: " + dto.attributeId());
            }
            AttributeOption option = attributeOptionRepository.findById(dto.optionId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "AttributeOption not found by id: " + dto.optionId()));
            pa.setOptionValue(option);
            pa.setCustomValue(null);
        } else {
            if (dto.customValue() == null || dto.customValue().isBlank()) {
                throw new IllegalArgumentException(
                        "customValue required for TEXT/NUMBER attribute id: "
                                + dto.attributeId());
            }
            pa.setCustomValue(dto.customValue());
            pa.setOptionValue(null);
        }
        return pa;
    }

    private ProductAttribute getProductAttribute(Long id) {
        return productAttributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }
}
