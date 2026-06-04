package vdm.shop.service.order.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdm.shop.dto.product.ProductResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.ProductMapper;
import vdm.shop.model.Favorite;
import vdm.shop.model.Product;
import vdm.shop.model.User;
import vdm.shop.repository.order.FavoriteRepository;
import vdm.shop.repository.product.ProductRepository;
import vdm.shop.repository.user.UserRepository;
import vdm.shop.service.order.FavoriteService;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDto> getMyFavorites(Long userId) {
        return favoriteRepository.findByUserIdWithProducts(userId).stream()
                .map(f -> productMapper.toDto(f.getProduct()))
                .toList();
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long userId, Long productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            favoriteRepository.deleteByUserIdAndProductId(userId, productId);
            log.info("Removed product {} from favorites for user {}", productId, userId);
            return false; // removed
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product not found: " + productId)
                    );
            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setProduct(product);
            favoriteRepository.save(fav);
            log.info("Added product {} to favorites for user {}", productId, userId);
            return true; // added
        }
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
}
