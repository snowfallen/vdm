package vdm.shop.repository.product;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p JOIN FETCH p.productGroup "
            + "WHERE p.productGroup.id = :productGroupId")
    List<Product> findAllByProductGroupId(Long productGroupId);

    @Query("SELECT p FROM Product p JOIN FETCH p.productGroup")
    Page<Product> findAllWithProductGroup(Pageable pageable);
}
