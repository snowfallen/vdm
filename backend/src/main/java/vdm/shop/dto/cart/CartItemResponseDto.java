package vdm.shop.dto.cart;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal pricePerUnit;
    private Integer quantity;
    private BigDecimal subTotal;
}
