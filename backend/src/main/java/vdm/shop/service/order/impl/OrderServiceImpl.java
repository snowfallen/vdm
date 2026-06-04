package vdm.shop.service.order.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdm.shop.dto.order.OrderItemResponseDto;
import vdm.shop.dto.order.OrderResponseDto;
import vdm.shop.dto.order.PlaceOrderRequestDto;
import vdm.shop.dto.order.UpdateOrderStatusRequestDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.model.CartItem;
import vdm.shop.model.Order;
import vdm.shop.model.OrderItem;
import vdm.shop.model.ShoppingCart;
import vdm.shop.model.User;
import vdm.shop.model.enumeration.OrderStatus;
import vdm.shop.model.enumeration.PaymentStatus;
import vdm.shop.repository.cart.ShoppingCartRepository;
import vdm.shop.repository.client.ClientRepository;
import vdm.shop.repository.order.OrderRepository;
import vdm.shop.repository.user.UserRepository;
import vdm.shop.service.order.OrderService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository cartRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(Long userId, PlaceOrderRequestDto dto) {
        log.info("Placing order for user {}", userId);

        ShoppingCart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart not found for user: " + userId)
                );

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order with empty cart");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Order order = new Order();
        order.setUser(user);

        // Адреса доставки — беремо з DTO або з профілю клієнта
        if (dto.getDeliveryCity() != null && !dto.getDeliveryCity().isBlank()) {
            order.setDeliveryCountry(dto.getDeliveryCountry());
            order.setDeliveryCity(dto.getDeliveryCity());
            order.setDeliveryStreet(dto.getDeliveryStreet());
            order.setDeliveryHouse(dto.getDeliveryHouse());
            order.setDeliveryApartment(dto.getDeliveryApartment());
            order.setDeliveryPostalCode(dto.getDeliveryPostalCode());
        } else {
            // Беремо адресу з профілю клієнта
            clientRepository.findByUserId(userId).ifPresent(client -> {
                order.setDeliveryCountry(client.getCountry());
                order.setDeliveryCity(client.getCity());
                order.setDeliveryStreet(client.getStreet());
                order.setDeliveryHouse(client.getHouseNumber());
                order.setDeliveryApartment(client.getApartmentNumber());
                order.setDeliveryPostalCode(client.getPostalCode());
            });
        }

        order.setComment(dto.getComment());

        // Переносимо товари з кошика в замовлення
        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setProductName(cartItem.getProduct().getName());
            item.setPricePerUnit(cartItem.getProduct().getPrice());
            item.setQuantity(cartItem.getQuantity());
            BigDecimal subTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setSubTotal(subTotal);
            total = total.add(subTotal);
            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalPrice(total.setScale(2, RoundingMode.HALF_UP));

        Order saved = orderRepository.save(order);

        // Очищаємо кошик після оформлення
        cart.getCartItems().clear();
        cartRepository.save(cart);

        log.info("Order {} placed successfully for user {}", saved.getId(), userId);
        return toDto(saved);
    }

    @Override
    public Page<OrderResponseDto> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdWithItems(userId, pageable).map(this::toDto);
    }

    @Override
    public OrderResponseDto getMyOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("Order does not belong to user " + userId);
        }
        return toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto payOrder(Long userId, Long orderId) {
        log.info("Mock payment for order {} by user {}", orderId, userId);
        Order order = getAndVerifyOwner(userId, orderId);

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Order already paid");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay cancelled order");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());

        return toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long userId, Long orderId) {
        Order order = getAndVerifyOwner(userId, orderId);
        if (order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel shipped/delivered order");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAllWithUser(pageable).map(this::toDto);
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        return toDto(orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId)));
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long orderId, UpdateOrderStatusRequestDto dto) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        order.setStatus(dto.getStatus());
        order.setUpdatedAt(LocalDateTime.now());
        return toDto(orderRepository.save(order));
    }

    private Order getAndVerifyOwner(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("Order does not belong to user " + userId);
        }
        return order;
    }

    private OrderResponseDto toDto(Order o) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(o.getId());
        dto.setUserId(o.getUser().getId());
        dto.setUserEmail(o.getUser().getEmail());
        dto.setUserFirstName(o.getUser().getFirstName());
        dto.setUserLastName(o.getUser().getLastName());
        dto.setStatus(o.getStatus());
        dto.setPaymentStatus(o.getPaymentStatus());
        dto.setTotalPrice(o.getTotalPrice());
        dto.setDeliveryCountry(o.getDeliveryCountry());
        dto.setDeliveryCity(o.getDeliveryCity());
        dto.setDeliveryStreet(o.getDeliveryStreet());
        dto.setDeliveryHouse(o.getDeliveryHouse());
        dto.setDeliveryApartment(o.getDeliveryApartment());
        dto.setDeliveryPostalCode(o.getDeliveryPostalCode());
        dto.setComment(o.getComment());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setUpdatedAt(o.getUpdatedAt());

        if (o.getItems() != null) {
            Set<OrderItemResponseDto> items = new HashSet<>();
            for (var item : o.getItems()) {
                OrderItemResponseDto i = new OrderItemResponseDto();
                i.setId(item.getId());
                i.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
                i.setProductName(item.getProductName());
                i.setPricePerUnit(item.getPricePerUnit());
                i.setQuantity(item.getQuantity());
                i.setSubTotal(item.getSubTotal());
                items.add(i);
            }
            dto.setItems(items);
        }
        return dto;
    }
}
