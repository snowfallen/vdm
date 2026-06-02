package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.product.ProductFilterRequestDto;
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

    @Operation(summary = "Create product")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto create(@RequestBody @Valid ProductRequestDto requestDto) {
        log.info("Creating product: {}", requestDto);
        return productService.create(requestDto);
    }

    @Operation(summary = "Get all products paginated")
    @GetMapping
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all products with pageable: {}", pageable);
        return productService.getAll(pageable);
    }

    @Operation(summary = "Get products by product group ID")
    @GetMapping("/product-group/{productGroupId}")
    public List<ProductResponseDto> getAllByProductGroupId(
            @PathVariable Long productGroupId) {
        log.info("Fetching products by productGroupId: {}", productGroupId);
        return productService.getAllByProductGroupId(productGroupId);
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable Long id) {
        log.info("Fetching product by id: {}", id);
        return productService.getById(id);
    }

    @Operation(summary = "Filter products by subcategory, attributes etc.",
            description = "AND між різними атрибутами, OR між значеннями одного атрибуту")
    @ApiResponse(responseCode = "200", description = "Successfully filtered products")
    @PostMapping("/filter")
    public Page<ProductResponseDto> getByFilter(
            @RequestBody @Valid ProductFilterRequestDto filter,
            Pageable pageable) {
        log.info("Filtering products, pageable: {}", pageable);
        return productService.getByFilter(filter, pageable);
    }

    @Operation(summary = "Search products by query",
            description = "Пошук по назві + значеннях атрибутів. Кешується в Redis.")
    @GetMapping("/search")
    public Page<ProductResponseDto> search(
            @RequestParam String q,
            Pageable pageable) {
        log.info("Searching products by query: '{}'", q);
        return productService.search(q, pageable);
    }

    @Operation(summary = "Update product by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto update(@PathVariable Long id,
                                      @RequestBody @Valid ProductRequestDto requestDto) {
        log.info("Updating product with id: {}", id);
        return productService.update(id, requestDto);
    }

    @Operation(summary = "Delete product by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto delete(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        return productService.delete(id);
    }
}
