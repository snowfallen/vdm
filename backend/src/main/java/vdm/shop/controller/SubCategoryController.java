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
import vdm.shop.dto.subcategory.SubCategoryRequestDto;
import vdm.shop.dto.subcategory.SubCategoryResponseDto;
import vdm.shop.service.subcategory.SubCategoryService;

@Tag(name = "SubCategory management", description = "Endpoints for subCategory management")
@RestController
@RequestMapping("/sub-categories")
@RequiredArgsConstructor
@Slf4j
public class SubCategoryController {
    private final SubCategoryService subCategoryService;

    @Operation(summary = "Create a new subCategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubCategory created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SubCategoryResponseDto create(@RequestBody @Valid SubCategoryRequestDto requestDto) {
        log.info("Creating subCategory with data: {}", requestDto);
        return subCategoryService.create(requestDto);
    }

    @Operation(summary = "Get all subCategories paginated")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved subCategories")
    @GetMapping
    public Page<SubCategoryResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all subCategories with pageable: {}", pageable);
        return subCategoryService.getAll(pageable);
    }

    @Operation(summary = "Get all subCategories by category ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved subCategories")
    @GetMapping("/category/{categoryId}")
    public List<SubCategoryResponseDto> getAllByCategoryId(@PathVariable Long categoryId) {
        log.info("Fetching all subCategories by categoryId: {}", categoryId);
        return subCategoryService.getAllByCategoryId(categoryId);
    }

    @Operation(summary = "Get subCategory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved subCategory"),
            @ApiResponse(responseCode = "404", description = "SubCategory not found")
    })
    @GetMapping("/{id}")
    public SubCategoryResponseDto getById(@PathVariable Long id) {
        log.info("Fetching subCategory by id: {}", id);
        return subCategoryService.getById(id);
    }

    @Operation(summary = "Update subCategory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubCategory updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "SubCategory not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubCategoryResponseDto update(@PathVariable Long id,
                                         @RequestBody @Valid SubCategoryRequestDto requestDto) {
        log.info("Updating subCategory with id: {} and data: {}", id, requestDto);
        return subCategoryService.update(id, requestDto);
    }

    @Operation(summary = "Delete subCategory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubCategory deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "SubCategory not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubCategoryResponseDto delete(@PathVariable Long id) {
        log.info("Deleting subCategory with id: {}", id);
        return subCategoryService.delete(id);
    }
}
