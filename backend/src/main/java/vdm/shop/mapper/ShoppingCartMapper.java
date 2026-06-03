package vdm.shop.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.cart.CartItemResponseDto;
import vdm.shop.dto.cart.ShoppingCartResponseDto;
import vdm.shop.model.ShoppingCart;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", source = "cartItems")
    @Mapping(target = "totalPrice", ignore = true)
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void calcTotal(@MappingTarget ShoppingCartResponseDto dto) {
        if (dto.getCartItems() == null || dto.getCartItems().isEmpty()) {
            dto.setTotalPrice(BigDecimal.ZERO);
            return;
        }
        BigDecimal total = dto.getCartItems().stream()
                .map(CartItemResponseDto::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalPrice(total.setScale(2, RoundingMode.HALF_UP));
    }
}
