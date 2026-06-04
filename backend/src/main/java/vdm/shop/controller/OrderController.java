package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.order.OrderResponseDto;
import vdm.shop.dto.order.PlaceOrderRequestDto;
import vdm.shop.dto.order.UpdateOrderStatusRequestDto;
import vdm.shop.model.User;
import vdm.shop.service.order.OrderService;

@Tag(name = "Orders", description = "Управління замовленнями")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    // ============ CLIENT endpoints ============

    @Operation(summary = "Place order from cart")
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENT')")
    public OrderResponseDto placeOrder(Authentication auth,
                                        @RequestBody PlaceOrderRequestDto dto) {
        return orderService.placeOrder(userId(auth), dto);
    }

    @Operation(summary = "Get my orders")
    @GetMapping("/orders/my")
    @PreAuthorize("hasRole('CLIENT')")
    public Page<OrderResponseDto> getMyOrders(Authentication auth, Pageable pageable) {
        return orderService.getMyOrders(userId(auth), pageable);
    }

    @Operation(summary = "Get my order by id")
    @GetMapping("/orders/my/{orderId}")
    @PreAuthorize("hasRole('CLIENT')")
    public OrderResponseDto getMyOrderById(Authentication auth,
                                            @PathVariable Long orderId) {
        return orderService.getMyOrderById(userId(auth), orderId);
    }

    @Operation(summary = "Pay order (mock)")
    @PostMapping("/orders/{orderId}/pay")
    @PreAuthorize("hasRole('CLIENT')")
    public OrderResponseDto payOrder(Authentication auth, @PathVariable Long orderId) {
        return orderService.payOrder(userId(auth), orderId);
    }

    @Operation(summary = "Cancel order")
    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    public OrderResponseDto cancelOrder(Authentication auth, @PathVariable Long orderId) {
        return orderService.cancelOrder(userId(auth), orderId);
    }

    // ============ ADMIN endpoints ============

    @Operation(summary = "Get all orders (admin)")
    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @Operation(summary = "Get order by id (admin)")
    @GetMapping("/admin/orders/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @Operation(summary = "Update order status (admin)")
    @PutMapping("/admin/orders/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDto updateStatus(@PathVariable Long orderId,
                                          @RequestBody @Valid UpdateOrderStatusRequestDto dto) {
        return orderService.updateStatus(orderId, dto);
    }

    private Long userId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
