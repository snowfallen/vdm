package vdm.shop.service.subcategory.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vdm.shop.dto.subcategory.SubCategoryRequestDto;
import vdm.shop.dto.subcategory.SubCategoryResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.SubCategoryMapper;
import vdm.shop.model.Category;
import vdm.shop.model.SubCategory;
import vdm.shop.repository.category.CategoryRepository;
import vdm.shop.repository.subcategory.SubCategoryRepository;
import vdm.shop.service.subcategory.SubCategoryService;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubCategoryServiceImpl implements SubCategoryService {
    private static final String SUB_CATEGORY_NOT_FOUND_BY_ID = "SubCategory not found by id: ";
    private static final String CATEGORY_NOT_FOUND_BY_ID = "Category not found by id: ";
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryMapper subCategoryMapper;

    @Override
    public SubCategoryResponseDto create(SubCategoryRequestDto requestDto) {
        log.info("Creating subCategory with name: {}", requestDto.name());
        SubCategory subCategory = subCategoryMapper.toModel(requestDto);
        subCategory.setCategory(getCategory(requestDto.categoryId()));
        SubCategory saved = subCategoryRepository.save(subCategory);
        log.info("SubCategory created with id: {}", saved.getId());
        return subCategoryMapper.toDto(saved);
    }

    @Override
    public Page<SubCategoryResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all subCategories with pageable: {}", pageable);
        return subCategoryMapper.toDtoPage(subCategoryRepository.findAllWithCategory(pageable));
    }

    @Override
    public List<SubCategoryResponseDto> getAllByCategoryId(Long categoryId) {
        log.info("Fetching all subCategories by categoryId: {}", categoryId);
        return subCategoryMapper.toDtoList(
                subCategoryRepository.findAllByCategoryId(categoryId));
    }

    @Override
    public SubCategoryResponseDto getById(Long id) {
        log.info("Fetching subCategory by id: {}", id);
        return subCategoryMapper.toDto(getSubCategory(id));
    }

    @Override
    public SubCategoryResponseDto update(Long id, SubCategoryRequestDto requestDto) {
        log.info("Updating subCategory with id: {}", id);
        SubCategory subCategory = getSubCategory(id);
        subCategory.setName(requestDto.name());
        subCategory.setCategory(getCategory(requestDto.categoryId()));
        SubCategory updated = subCategoryRepository.save(subCategory);
        log.info("SubCategory with id: {} updated successfully", id);
        return subCategoryMapper.toDto(updated);
    }

    @Override
    public SubCategoryResponseDto delete(Long id) {
        log.info("Deleting subCategory with id: {}", id);
        SubCategory subCategory = getSubCategory(id);
        SubCategoryResponseDto dto = subCategoryMapper.toDto(subCategory);
        subCategoryRepository.delete(subCategory);
        log.info("SubCategory with id: {} deleted successfully", id);
        return dto;
    }

    private SubCategory getSubCategory(Long id) {
        log.debug("Retrieving subCategory with id: {}", id);
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        SUB_CATEGORY_NOT_FOUND_BY_ID + id));
    }

    private Category getCategory(Long id) {
        log.debug("Retrieving category with id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_BY_ID + id));
    }
}
