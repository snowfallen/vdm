package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.product.ProductResponseDto;
import vdm.shop.model.User;
import vdm.shop.service.order.FavoriteService;

@Tag(name = "Favorites", description = "Обране клієнта")
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "Get my favorites")
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public List<ProductResponseDto> getMyFavorites(Authentication auth) {
        return favoriteService.getMyFavorites(userId(auth));
    }

    @Operation(summary = "Toggle favorite (add/remove)")
    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, Object> toggle(Authentication auth, @PathVariable Long productId) {
        boolean added = favoriteService.toggleFavorite(userId(auth), productId);
        return Map.of("added", added, "productId", productId);
    }

    @Operation(summary = "Check if product is favorite")
    @GetMapping("/{productId}/check")
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, Boolean> check(Authentication auth, @PathVariable Long productId) {
        return Map.of("favorite", favoriteService.isFavorite(userId(auth), productId));
    }

    private Long userId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
