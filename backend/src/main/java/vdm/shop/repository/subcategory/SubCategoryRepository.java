package vdm.shop.repository.subcategory;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    @Query("SELECT s FROM SubCategory s JOIN FETCH s.category WHERE s.category.id = :categoryId")
    List<SubCategory> findAllByCategoryId(Long categoryId);

    @Query("SELECT s FROM SubCategory s JOIN FETCH s.category")
    Page<SubCategory> findAllWithCategory(Pageable pageable);
}
