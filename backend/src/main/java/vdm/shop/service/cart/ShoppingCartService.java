package vdm.shop.service.cart;

import vdm.shop.dto.cart.CartItemRequestDto;
import vdm.shop.dto.cart.ShoppingCartResponseDto;
import vdm.shop.dto.cart.UpdateCartItemRequestDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto getCart(Long userId);

    ShoppingCartResponseDto addItem(Long userId, CartItemRequestDto dto);

    ShoppingCartResponseDto updateItem(Long userId, Long cartItemId,
                                       UpdateCartItemRequestDto dto);

    ShoppingCartResponseDto removeItem(Long userId, Long cartItemId);

    void clearCart(Long userId);
}
