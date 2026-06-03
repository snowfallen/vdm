package vdm.shop.repository.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.ShoppingCart;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @Query("SELECT sc FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.cartItems ci "
            + "LEFT JOIN FETCH ci.product "
            + "WHERE sc.user.id = :userId")
    Optional<ShoppingCart> findByUserIdWithItems(Long userId);

    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.user.id = :userId")
    Optional<ShoppingCart> findByUserId(Long userId);
}
