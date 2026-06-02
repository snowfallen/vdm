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
import vdm.shop.dto.productgroup.ProductGroupRequestDto;
import vdm.shop.dto.productgroup.ProductGroupResponseDto;
import vdm.shop.service.productgroup.ProductGroupService;

@Tag(name = "ProductGroup management", description = "Endpoints for productGroup management")
@RestController
@RequestMapping("/product-groups")
@RequiredArgsConstructor
@Slf4j
public class ProductGroupController {
    private final ProductGroupService productGroupService;

    @Operation(summary = "Create a new productGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ProductGroup created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductGroupResponseDto create(@RequestBody @Valid ProductGroupRequestDto requestDto) {
        log.info("Creating productGroup with data: {}", requestDto);
        return productGroupService.create(requestDto);
    }

    @Operation(summary = "Get all productGroups paginated")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved productGroups")
    @GetMapping
    public Page<ProductGroupResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all productGroups with pageable: {}", pageable);
        return productGroupService.getAll(pageable);
    }

    @Operation(summary = "Get all productGroups by subCategory ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved productGroups")
    @GetMapping("/sub-category/{subCategoryId}")
    public List<ProductGroupResponseDto> getAllBySubCategoryId(
            @PathVariable Long subCategoryId) {
        log.info("Fetching all productGroups by subCategoryId: {}", subCategoryId);
        return productGroupService.getAllBySubCategoryId(subCategoryId);
    }

    @Operation(summary = "Get productGroup by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved productGroup"),
            @ApiResponse(responseCode = "404", description = "ProductGroup not found")
    })
    @GetMapping("/{id}")
    public ProductGroupResponseDto getById(@PathVariable Long id) {
        log.info("Fetching productGroup by id: {}", id);
        return productGroupService.getById(id);
    }

    @Operation(summary = "Update productGroup by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ProductGroup updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "ProductGroup not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductGroupResponseDto update(@PathVariable Long id,
                                          @RequestBody @Valid ProductGroupRequestDto requestDto) {
        log.info("Updating productGroup with id: {} and data: {}", id, requestDto);
        return productGroupService.update(id, requestDto);
    }

    @Operation(summary = "Delete productGroup by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ProductGroup deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "ProductGroup not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductGroupResponseDto delete(@PathVariable Long id) {
        log.info("Deleting productGroup with id: {}", id);
        return productGroupService.delete(id);
    }
}
