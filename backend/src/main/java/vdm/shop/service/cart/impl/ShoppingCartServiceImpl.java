package vdm.shop.service.cart.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdm.shop.dto.cart.CartItemRequestDto;
import vdm.shop.dto.cart.ShoppingCartResponseDto;
import vdm.shop.dto.cart.UpdateCartItemRequestDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.ShoppingCartMapper;
import vdm.shop.model.CartItem;
import vdm.shop.model.Product;
import vdm.shop.model.ShoppingCart;
import vdm.shop.model.User;
import vdm.shop.repository.cart.CartItemRepository;
import vdm.shop.repository.cart.ShoppingCartRepository;
import vdm.shop.repository.product.ProductRepository;
import vdm.shop.repository.user.UserRepository;
import vdm.shop.service.cart.ShoppingCartService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public ShoppingCartResponseDto getCart(Long userId) {
        log.debug("Fetching cart for user {}", userId);
        ShoppingCart cart = findOrCreate(userId);
        return shoppingCartMapper.toDto(
                shoppingCartRepository.findByUserIdWithItems(cart.getUser().getId())
                        .orElse(cart));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto addItem(Long userId, CartItemRequestDto dto) {
        log.info("addItem: userId={}, productId={}, qty={}", userId, dto.getProductId(),
                dto.getQuantity());

        ShoppingCart cart = findOrCreate(userId);

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found: " + dto.getProductId()));

        // Якщо товар вже є — збільшуємо кількість
        Optional<CartItem> existing = cartItemRepository
                .findByShoppingCartIdAndProductId(cart.getId(), product.getId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            cartItemRepository.save(item);
            log.info("Updated qty for cartItem {}", item.getId());
        } else {
            CartItem item = new CartItem();
            item.setShoppingCart(cart);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            cart.addCartItem(item);
            cartItemRepository.save(item);
            log.info("Added new cartItem for product {}", product.getId());
        }

        return shoppingCartMapper.toDto(
                shoppingCartRepository.findByUserIdWithItems(userId)
                        .orElseThrow());
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateItem(Long userId, Long cartItemId,
                                              UpdateCartItemRequestDto dto) {
        log.info("updateItem: userId={}, cartItemId={}, qty={}", userId, cartItemId,
                dto.getQuantity());

        CartItem item = getItemAndVerifyOwner(userId, cartItemId);
        item.setQuantity(dto.getQuantity());
        cartItemRepository.save(item);

        return shoppingCartMapper.toDto(
                shoppingCartRepository.findByUserIdWithItems(userId).orElseThrow());
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto removeItem(Long userId, Long cartItemId) {
        log.info("removeItem: userId={}, cartItemId={}", userId, cartItemId);

        ShoppingCart cart = findOrCreate(userId);
        CartItem item = getItemAndVerifyOwner(userId, cartItemId);
        cart.removeCartItem(item);
        shoppingCartRepository.save(cart);

        return shoppingCartMapper.toDto(
                shoppingCartRepository.findByUserIdWithItems(userId).orElse(cart));
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("clearCart: userId={}", userId);
        shoppingCartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getCartItems().clear();
            shoppingCartRepository.save(cart);
        });
    }

    private ShoppingCart findOrCreate(Long userId) {
        return shoppingCartRepository.findByUserId(userId).orElseGet(() -> {
            log.warn("Creating new cart for user {}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
            ShoppingCart cart = new ShoppingCart();
            cart.setUser(user);
            return shoppingCartRepository.save(cart);
        });
    }

    private CartItem getItemAndVerifyOwner(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "CartItem not found: " + cartItemId));
        if (!item.getShoppingCart().getUser().getId().equals(userId)) {
            throw new SecurityException("CartItem does not belong to user " + userId);
        }
        return item;
    }
}
