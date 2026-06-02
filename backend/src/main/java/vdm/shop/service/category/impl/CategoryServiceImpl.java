package vdm.shop.service.category.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vdm.shop.dto.category.CategoryRequestDto;
import vdm.shop.dto.category.CategoryResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.CategoryMapper;
import vdm.shop.model.Category;
import vdm.shop.repository.category.CategoryRepository;
import vdm.shop.service.category.CategoryService;

@RequiredArgsConstructor
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY_NOT_FOUND_BY_ID = "Category not found by id: ";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto create(CategoryRequestDto requestDto) {
        log.info("Creating category with name: {}", requestDto.name());
        Category category = categoryMapper.toModel(requestDto);
        Category saved = categoryRepository.save(category);
        log.info("Category created with id: {}", saved.getId());
        return categoryMapper.toDto(saved);
    }

    @Override
    public Page<CategoryResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all categories with pageable: {}", pageable);
        return categoryMapper.toDtoPage(categoryRepository.findAll(pageable));
    }

    @Override
    public List<CategoryResponseDto> getAllList() {
        log.info("Fetching all categories as list");
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        log.info("Fetching category by id: {}", id);
        return categoryMapper.toDto(getCategory(id));
    }

    @Override
    public CategoryResponseDto update(Long id, CategoryRequestDto requestDto) {
        log.info("Updating category with id: {}", id);
        Category category = getCategory(id);
        category.setName(requestDto.name());
        Category updated = categoryRepository.save(category);
        log.info("Category with id: {} updated successfully", id);
        return categoryMapper.toDto(updated);
    }

    @Override
    public CategoryResponseDto delete(Long id) {
        log.info("Deleting category with id: {}", id);
        Category category = getCategory(id);
        CategoryResponseDto dto = categoryMapper.toDto(category);
        categoryRepository.delete(category);
        log.info("Category with id: {} deleted successfully", id);
        return dto;
    }

    private Category getCategory(Long id) {
        log.debug("Retrieving category with id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_BY_ID + id));
    }
}
