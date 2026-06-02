package vdm.shop.repository.productgroup;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.ProductGroup;

@Repository
public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {
    @Query("SELECT pg FROM ProductGroup pg JOIN FETCH pg.subCategory "
            + "WHERE pg.subCategory.id = :subCategoryId")
    List<ProductGroup> findAllBySubCategoryId(Long subCategoryId);

    @Query("SELECT pg FROM ProductGroup pg JOIN FETCH pg.subCategory")
    Page<ProductGroup> findAllWithSubCategory(Pageable pageable);
}
