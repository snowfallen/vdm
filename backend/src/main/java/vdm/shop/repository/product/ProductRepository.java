package vdm.shop.repository.product;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.Product;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p JOIN FETCH p.productGroup "
            + "WHERE p.productGroup.id = :productGroupId")
    List<Product> findAllByProductGroupId(Long productGroupId);

    @Query(
            value = "SELECT p FROM Product p JOIN FETCH p.productGroup",
            countQuery = "SELECT COUNT(p) FROM Product p"
    )
    Page<Product> findAllWithProductGroup(Pageable pageable);

    @Query(
            value = "SELECT p FROM Product p JOIN FETCH p.productGroup "
                    + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))",
            countQuery = "SELECT COUNT(p) FROM Product p "
                    + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))"
    )
    Page<Product> searchByQuery(String query, Pageable pageable);
}
