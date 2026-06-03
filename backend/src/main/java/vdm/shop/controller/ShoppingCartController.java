package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.cart.CartItemRequestDto;
import vdm.shop.dto.cart.ShoppingCartResponseDto;
import vdm.shop.dto.cart.UpdateCartItemRequestDto;
import vdm.shop.model.User;
import vdm.shop.service.cart.ShoppingCartService;

@Tag(name = "Shopping Cart", description = "Кошик покупця")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get cart")
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ShoppingCartResponseDto getCart(Authentication auth) {
        return shoppingCartService.getCart(userId(auth));
    }

    @Operation(summary = "Add item")
    @PostMapping("/items")
    @PreAuthorize("hasRole('CLIENT')")
    public ShoppingCartResponseDto addItem(Authentication auth,
                                           @RequestBody @Valid CartItemRequestDto dto) {
        return shoppingCartService.addItem(userId(auth), dto);
    }

    @Operation(summary = "Update item quantity")
    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ShoppingCartResponseDto updateItem(Authentication auth,
                                              @PathVariable Long cartItemId,
                                              @RequestBody @Valid UpdateCartItemRequestDto dto) {
        return shoppingCartService.updateItem(userId(auth), cartItemId, dto);
    }

    @Operation(summary = "Remove item")
    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ShoppingCartResponseDto removeItem(Authentication auth,
                                              @PathVariable Long cartItemId) {
        return shoppingCartService.removeItem(userId(auth), cartItemId);
    }

    @Operation(summary = "Clear cart")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('CLIENT')")
    public void clearCart(Authentication auth) {
        shoppingCartService.clearCart(userId(auth));
    }

    private Long userId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
