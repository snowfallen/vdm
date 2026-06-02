package vdm.shop.service.productgroup.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vdm.shop.dto.productgroup.ProductGroupRequestDto;
import vdm.shop.dto.productgroup.ProductGroupResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.ProductGroupMapper;
import vdm.shop.model.ProductGroup;
import vdm.shop.model.SubCategory;
import vdm.shop.repository.productgroup.ProductGroupRepository;
import vdm.shop.repository.subcategory.SubCategoryRepository;
import vdm.shop.service.productgroup.ProductGroupService;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductGroupServiceImpl implements ProductGroupService {
    private static final String PRODUCT_GROUP_NOT_FOUND_BY_ID = "ProductGroup not found by id: ";
    private static final String SUB_CATEGORY_NOT_FOUND_BY_ID = "SubCategory not found by id: ";
    private final ProductGroupRepository productGroupRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductGroupMapper productGroupMapper;

    @Override
    public ProductGroupResponseDto create(ProductGroupRequestDto requestDto) {
        log.info("Creating productGroup with name: {}", requestDto.name());
        ProductGroup productGroup = productGroupMapper.toModel(requestDto);
        productGroup.setSubCategory(getSubCategory(requestDto.subCategoryId()));
        ProductGroup saved = productGroupRepository.save(productGroup);
        log.info("ProductGroup created with id: {}", saved.getId());
        return productGroupMapper.toDto(saved);
    }

    @Override
    public Page<ProductGroupResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all productGroups with pageable: {}", pageable);
        return productGroupMapper.toDtoPage(
                productGroupRepository.findAllWithSubCategory(pageable));
    }

    @Override
    public List<ProductGroupResponseDto> getAllBySubCategoryId(Long subCategoryId) {
        log.info("Fetching all productGroups by subCategoryId: {}", subCategoryId);
        return productGroupMapper.toDtoList(
                productGroupRepository.findAllBySubCategoryId(subCategoryId));
    }

    @Override
    public ProductGroupResponseDto getById(Long id) {
        log.info("Fetching productGroup by id: {}", id);
        return productGroupMapper.toDto(getProductGroup(id));
    }

    @Override
    public ProductGroupResponseDto update(Long id, ProductGroupRequestDto requestDto) {
        log.info("Updating productGroup with id: {}", id);
        ProductGroup productGroup = getProductGroup(id);
        productGroup.setName(requestDto.name());
        productGroup.setSubCategory(getSubCategory(requestDto.subCategoryId()));
        ProductGroup updated = productGroupRepository.save(productGroup);
        log.info("ProductGroup with id: {} updated successfully", id);
        return productGroupMapper.toDto(updated);
    }

    @Override
    public ProductGroupResponseDto delete(Long id) {
        log.info("Deleting productGroup with id: {}", id);
        ProductGroup productGroup = getProductGroup(id);
        ProductGroupResponseDto dto = productGroupMapper.toDto(productGroup);
        productGroupRepository.delete(productGroup);
        log.info("ProductGroup with id: {} deleted successfully", id);
        return dto;
    }

    private ProductGroup getProductGroup(Long id) {
        log.debug("Retrieving productGroup with id: {}", id);
        return productGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        PRODUCT_GROUP_NOT_FOUND_BY_ID + id));
    }

    private SubCategory getSubCategory(Long id) {
        log.debug("Retrieving subCategory with id: {}", id);
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        SUB_CATEGORY_NOT_FOUND_BY_ID + id));
    }
}
