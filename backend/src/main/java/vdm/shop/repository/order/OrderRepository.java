package vdm.shop.repository.order;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(
            "SELECT o FROM Order o "
            + "JOIN FETCH o.user "
            + "LEFT JOIN FETCH o.items i "
            + "LEFT JOIN FETCH i.product "
            + "WHERE o.user.id = :userId "
            + "ORDER BY o.createdAt DESC"
    )
    Page<Order> findByUserIdWithItems(Long userId, Pageable pageable);

    @Query(
            "SELECT o FROM Order o "
            + "JOIN FETCH o.user "
            + "LEFT JOIN FETCH o.items i "
            + "LEFT JOIN FETCH i.product "
            + "WHERE o.id = :id"
    )
    Optional<Order> findByIdWithItems(Long id);

    @Query(value = "SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.createdAt DESC",
            countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findAllWithUser(Pageable pageable);
}
