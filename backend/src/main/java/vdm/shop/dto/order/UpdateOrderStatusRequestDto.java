package vdm.shop.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vdm.shop.model.enumeration.OrderStatus;

@Getter
@Setter
public class UpdateOrderStatusRequestDto {
    @NotNull
    private OrderStatus status;
}
