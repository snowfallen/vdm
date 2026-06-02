package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.category.CategoryRequestDto;
import vdm.shop.dto.category.CategoryResponseDto;
import vdm.shop.service.category.CategoryService;

@Tag(name = "Category management", description = "Endpoints for category management")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDto create(@RequestBody @Valid CategoryRequestDto requestDto) {
        log.info("Creating category with data: {}", requestDto);
        return categoryService.create(requestDto);
    }

    @Operation(summary = "Get all categories paginated")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    @GetMapping
    public Page<CategoryResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all categories with pageable: {}", pageable);
        return categoryService.getAll(pageable);
    }

    @Operation(summary = "Get all categories as list")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    @GetMapping("/list")
    public List<CategoryResponseDto> getAllList() {
        log.info("Fetching all categories as list");
        return categoryService.getAllList();
    }

    @Operation(summary = "Get category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved category"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public CategoryResponseDto getById(@PathVariable Long id) {
        log.info("Fetching category by id: {}", id);
        return categoryService.getById(id);
    }

    @Operation(summary = "Update category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDto update(@PathVariable Long id,
                                      @RequestBody @Valid CategoryRequestDto requestDto) {
        log.info("Updating category with id: {} and data: {}", id, requestDto);
        return categoryService.update(id, requestDto);
    }

    @Operation(summary = "Delete category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDto delete(@PathVariable Long id) {
        log.info("Deleting category with id: {}", id);
        return categoryService.delete(id);
    }
}
