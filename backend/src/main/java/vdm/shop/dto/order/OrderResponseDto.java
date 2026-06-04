package vdm.shop.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import vdm.shop.model.enumeration.OrderStatus;
import vdm.shop.model.enumeration.PaymentStatus;

@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalPrice;
    private String deliveryCountry;
    private String deliveryCity;
    private String deliveryStreet;
    private String deliveryHouse;
    private String deliveryApartment;
    private String deliveryPostalCode;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<OrderItemResponseDto> items;
}
