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
import vdm.shop.dto.product.ProductRequestDto;
import vdm.shop.dto.product.ProductResponseDto;
import vdm.shop.service.product.ProductService;

@Tag(name = "Product management", description = "Endpoints for product management")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto create(@RequestBody @Valid ProductRequestDto requestDto) {
        log.info("Creating product with data: {}", requestDto);
        return productService.create(requestDto);
    }

    @Operation(summary = "Get all products paginated")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all products with pageable: {}", pageable);
        return productService.getAll(pageable);
    }

    @Operation(summary = "Get all products by productGroup ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping("/product-group/{productGroupId}")
    public List<ProductResponseDto> getAllByProductGroupId(@PathVariable Long productGroupId) {
        log.info("Fetching all products by productGroupId: {}", productGroupId);
        return productService.getAllByProductGroupId(productGroupId);
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable Long id) {
        log.info("Fetching product by id: {}", id);
        return productService.getById(id);
    }

    @Operation(summary = "Update product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto update(@PathVariable Long id,
                                     @RequestBody @Valid ProductRequestDto requestDto) {
        log.info("Updating product with id: {} and data: {}", id, requestDto);
        return productService.update(id, requestDto);
    }

    @Operation(summary = "Delete product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto delete(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        return productService.delete(id);
    }
}
