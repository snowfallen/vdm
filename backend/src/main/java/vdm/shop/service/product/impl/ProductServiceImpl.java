package vdm.shop.service.product.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vdm.shop.dto.product.ProductRequestDto;
import vdm.shop.dto.product.ProductResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.ProductMapper;
import vdm.shop.model.Product;
import vdm.shop.model.ProductGroup;
import vdm.shop.repository.product.ProductRepository;
import vdm.shop.repository.productgroup.ProductGroupRepository;
import vdm.shop.service.product.ProductService;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private static final String PRODUCT_NOT_FOUND_BY_ID = "Product not found by id: ";
    private static final String PRODUCT_GROUP_NOT_FOUND_BY_ID = "ProductGroup not found by id: ";
    private final ProductRepository productRepository;
    private final ProductGroupRepository productGroupRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto create(ProductRequestDto requestDto) {
        log.info("Creating product with name: {}", requestDto.name());
        Product product = productMapper.toModel(requestDto);
        product.setProductGroup(getProductGroup(requestDto.productGroupId()));
        Product saved = productRepository.save(product);
        log.info("Product created with id: {}", saved.getId());
        return productMapper.toDto(saved);
    }

    @Override
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all products with pageable: {}", pageable);
        return productMapper.toDtoPage(productRepository.findAllWithProductGroup(pageable));
    }

    @Override
    public List<ProductResponseDto> getAllByProductGroupId(Long productGroupId) {
        log.info("Fetching all products by productGroupId: {}", productGroupId);
        return productMapper.toDtoList(
                productRepository.findAllByProductGroupId(productGroupId));
    }

    @Override
    public ProductResponseDto getById(Long id) {
        log.info("Fetching product by id: {}", id);
        return productMapper.toDto(getProduct(id));
    }

    @Override
    public ProductResponseDto update(Long id, ProductRequestDto requestDto) {
        log.info("Updating product with id: {}", id);
        Product product = getProduct(id);
        product.setName(requestDto.name());
        product.setPrice(requestDto.price());
        product.setProductGroup(getProductGroup(requestDto.productGroupId()));
        Product updated = productRepository.save(product);
        log.info("Product with id: {} updated successfully", id);
        return productMapper.toDto(updated);
    }

    @Override
    public ProductResponseDto delete(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = getProduct(id);
        ProductResponseDto dto = productMapper.toDto(product);
        productRepository.delete(product);
        log.info("Product with id: {} deleted successfully", id);
        return dto;
    }

    private Product getProduct(Long id) {
        log.debug("Retrieving product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND_BY_ID + id));
    }

    private ProductGroup getProductGroup(Long id) {
        log.debug("Retrieving productGroup with id: {}", id);
        return productGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        PRODUCT_GROUP_NOT_FOUND_BY_ID + id));
    }
}
