package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.productattribute.ProductAttributeRequestDto;
import vdm.shop.dto.productattribute.ProductAttributeResponseDto;
import vdm.shop.service.productattribute.ProductAttributeService;

@Tag(name = "ProductAttribute management",
        description = "Endpoints for linking attributes to products")
@RestController
@RequestMapping("/product-attributes")
@RequiredArgsConstructor
@Slf4j
public class ProductAttributeController {
    private final ProductAttributeService productAttributeService;

    @Operation(summary = "Add attribute to product")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductAttributeResponseDto create(
            @RequestBody @Valid ProductAttributeRequestDto requestDto) {
        log.info("Creating productAttribute: {}", requestDto);
        return productAttributeService.create(requestDto);
    }

    @Operation(summary = "Get all attributes for product")
    @GetMapping("/product/{productId}")
    public List<ProductAttributeResponseDto> getAllByProductId(@PathVariable Long productId) {
        log.info("Fetching productAttributes for product id: {}", productId);
        return productAttributeService.getAllByProductId(productId);
    }

    @Operation(summary = "Get productAttribute by ID")
    @GetMapping("/{id}")
    public ProductAttributeResponseDto getById(@PathVariable Long id) {
        log.info("Fetching productAttribute by id: {}", id);
        return productAttributeService.getById(id);
    }

    @Operation(summary = "Update productAttribute by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductAttributeResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid ProductAttributeRequestDto requestDto) {
        log.info("Updating productAttribute with id: {}", id);
        return productAttributeService.update(id, requestDto);
    }

    @Operation(summary = "Delete productAttribute by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductAttributeResponseDto delete(@PathVariable Long id) {
        log.info("Deleting productAttribute with id: {}", id);
        return productAttributeService.delete(id);
    }
}
