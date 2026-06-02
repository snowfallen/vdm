package vdm.shop.service.product.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vdm.shop.dto.product.ProductFilterRequestDto;
import vdm.shop.dto.product.ProductRequestDto;
import vdm.shop.dto.product.ProductResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.ProductMapper;
import vdm.shop.model.Product;
import vdm.shop.model.ProductGroup;
import vdm.shop.repository.product.ProductRepository;
import vdm.shop.repository.productgroup.ProductGroupRepository;
import vdm.shop.repository.specification.SpecificationBuilder;
import vdm.shop.service.product.ProductService;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private static final String PRODUCT_NOT_FOUND_BY_ID = "Product not found by id: ";
    private static final String PRODUCT_GROUP_NOT_FOUND_BY_ID =
            "ProductGroup not found by id: ";

    private final ProductRepository productRepository;
    private final ProductGroupRepository productGroupRepository;
    private final ProductMapper productMapper;
    private final SpecificationBuilder<Product, ProductFilterRequestDto>
            productSpecificationBuilder;

    @Override
    public ProductResponseDto create(ProductRequestDto requestDto) {
        log.info("Creating product: {}", requestDto.name());
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
        log.info("Fetching products by productGroupId: {}", productGroupId);
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
        log.info("Product with id: {} updated", id);
        return productMapper.toDto(updated);
    }

    @Override
    public ProductResponseDto delete(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = getProduct(id);
        ProductResponseDto dto = productMapper.toDto(product);
        productRepository.delete(product);
        log.info("Product with id: {} deleted", id);
        return dto;
    }

    @Override
    public Page<ProductResponseDto> getByFilter(ProductFilterRequestDto filter,
                                                 Pageable pageable) {
        log.info("Filtering products with filter: {}", filter);
        Specification<Product> spec = productSpecificationBuilder.build(filter);
        return productMapper.toDtoPage(productRepository.findAll(spec, pageable));
    }

    @Override
    @Cacheable(value = "productSearch", key = "#query + '_' + #pageable.pageNumber")
    public Page<ProductResponseDto> search(String query, Pageable pageable) {
        log.info("Searching products by query: '{}'", query);
        return productMapper.toDtoPage(
                productRepository.searchByQuery(query.trim(), pageable));
    }

    @Override
    public ProductResponseDto getNewArrivals(int size) {
        log.info("Fetching new arrivals, size: {}", size);
        Page<Product> page = productRepository.findAllWithProductGroup(
                PageRequest.of(0, size, Sort.by("id").descending()));
        return productMapper.toDto(page.getContent().isEmpty()
                ? null : page.getContent().get(0));
    }

    @Override
    public ProductResponseDto getBestsellers(int size) {
        log.info("Fetching bestsellers, size: {}", size);
        Page<Product> page = productRepository.findAllWithProductGroup(
                PageRequest.of(0, size, Sort.by("price").descending()));
        return productMapper.toDto(page.getContent().isEmpty()
                ? null : page.getContent().get(0));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        PRODUCT_NOT_FOUND_BY_ID + id));
    }

    private ProductGroup getProductGroup(Long id) {
        return productGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        PRODUCT_GROUP_NOT_FOUND_BY_ID + id));
    }
}
