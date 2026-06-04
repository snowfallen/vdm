package vdm.shop.dto.order;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal pricePerUnit;
    private Integer quantity;
    private BigDecimal subTotal;
}
