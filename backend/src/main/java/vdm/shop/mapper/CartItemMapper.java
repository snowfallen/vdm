package vdm.shop.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.cart.CartItemResponseDto;
import vdm.shop.model.CartItem;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "pricePerUnit", source = "product.price")
    @Mapping(target = "subTotal", ignore = true)
    CartItemResponseDto toDto(CartItem cartItem);

    @AfterMapping
    default void calcSubTotal(@MappingTarget CartItemResponseDto dto, CartItem item) {
        if (item.getProduct() != null && item.getQuantity() != null) {
            BigDecimal price = item.getProduct().getPrice();
            dto.setSubTotal(
                    price.multiply(BigDecimal.valueOf(item.getQuantity()))
                            .setScale(2, RoundingMode.HALF_UP)
            );
        }
    }
}
