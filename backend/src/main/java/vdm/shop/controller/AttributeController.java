package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import vdm.shop.dto.attribute.AttributeRequestDto;
import vdm.shop.dto.attribute.AttributeResponseDto;
import vdm.shop.dto.attribute.AttributeWithOptionsResponseDto;
import vdm.shop.dto.attribute.SubCategoryFiltersResponseDto;
import vdm.shop.service.attribute.AttributeService;

@Tag(name = "Attribute management", description = "Endpoints for product attributes")
@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@Slf4j
public class AttributeController {
    private final AttributeService attributeService;

    @Operation(summary = "Create attribute")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AttributeResponseDto create(@RequestBody @Valid AttributeRequestDto requestDto) {
        log.info("Creating attribute: {}", requestDto);
        return attributeService.create(requestDto);
    }

    @Operation(summary = "Get all attributes paginated")
    @GetMapping
    public Page<AttributeResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all attributes with pageable: {}", pageable);
        return attributeService.getAll(pageable);
    }

    @Operation(summary = "Get all attributes as list")
    @GetMapping("/list")
    public List<AttributeResponseDto> getAllList() {
        log.info("Fetching all attributes as list");
        return attributeService.getAllList();
    }

    @Operation(summary = "Get attribute by ID")
    @GetMapping("/{id}")
    public AttributeResponseDto getById(@PathVariable Long id) {
        log.info("Fetching attribute by id: {}", id);
        return attributeService.getById(id);
    }

    @Operation(summary = "Get attribute with its options")
    @GetMapping("/{id}/with-options")
    public AttributeWithOptionsResponseDto getWithOptions(@PathVariable Long id) {
        log.info("Fetching attribute with options, id: {}", id);
        return attributeService.getWithOptions(id);
    }

    @Operation(summary = "Get filters for subcategory — used on catalog page")
    @GetMapping("/sub-category/{subCategoryId}/filters")
    public SubCategoryFiltersResponseDto getFiltersForSubCategory(
            @PathVariable Long subCategoryId) {
        log.info("Fetching filters for subCategoryId: {}", subCategoryId);
        return attributeService.getFiltersForSubCategory(subCategoryId);
    }

    @Operation(summary = "Get filters for product group")
    @GetMapping("/product-group/{productGroupId}/filters")
    public SubCategoryFiltersResponseDto getFiltersForProductGroup(
            @PathVariable Long productGroupId) {
        log.info("Fetching filters for productGroupId: {}", productGroupId);
        return attributeService.getFiltersForProductGroup(productGroupId);
    }

    @Operation(summary = "Update attribute by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AttributeResponseDto update(@PathVariable Long id,
                                        @RequestBody @Valid AttributeRequestDto requestDto) {
        log.info("Updating attribute with id: {}", id);
        return attributeService.update(id, requestDto);
    }

    @Operation(summary = "Delete attribute by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AttributeResponseDto delete(@PathVariable Long id) {
        log.info("Deleting attribute with id: {}", id);
        return attributeService.delete(id);
    }
}
