package vdm.shop.service.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.order.OrderResponseDto;
import vdm.shop.dto.order.PlaceOrderRequestDto;
import vdm.shop.dto.order.UpdateOrderStatusRequestDto;

public interface OrderService {
    OrderResponseDto placeOrder(Long userId, PlaceOrderRequestDto dto);

    Page<OrderResponseDto> getMyOrders(Long userId, Pageable pageable);

    OrderResponseDto getMyOrderById(Long userId, Long orderId);

    OrderResponseDto payOrder(Long userId, Long orderId);

    OrderResponseDto cancelOrder(Long userId, Long orderId);

    Page<OrderResponseDto> getAllOrders(Pageable pageable);

    OrderResponseDto getOrderById(Long orderId);

    OrderResponseDto updateStatus(Long orderId, UpdateOrderStatusRequestDto dto);
}
