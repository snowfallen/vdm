package vdm.shop.service.order;

import java.util.List;
import vdm.shop.dto.product.ProductResponseDto;

public interface FavoriteService {
    List<ProductResponseDto> getMyFavorites(Long userId);

    boolean toggleFavorite(Long userId, Long productId);

    boolean isFavorite(Long userId, Long productId);
}
