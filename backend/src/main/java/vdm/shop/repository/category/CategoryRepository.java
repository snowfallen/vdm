package vdm.shop.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vdm.shop.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
