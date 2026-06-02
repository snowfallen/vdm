package vdm.shop.repository.productattribute;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vdm.shop.model.ProductAttribute;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    @Query("SELECT pa FROM ProductAttribute pa "
            + "JOIN FETCH pa.attribute a "
            + "LEFT JOIN FETCH a.unit "
            + "LEFT JOIN FETCH pa.optionValue "
            + "WHERE pa.product.id = :productId")
    List<ProductAttribute> findAllByProductId(Long productId);

    // Повертає всі унікальні пари (attributeId, value) для товарів підкатегорії
    // Використовується для побудови фільтрів на сторінці підкатегорії
    @Query("SELECT pa.attribute.id, pa.attribute.name, pa.attribute.dataType, "
            + "pa.attribute.unit.symbol, "
            + "COALESCE(pa.optionValue.value, pa.customValue) AS val "
            + "FROM ProductAttribute pa "
            + "JOIN pa.product p "
            + "JOIN p.productGroup pg "
            + "WHERE pg.subCategory.id = :subCategoryId "
            + "GROUP BY pa.attribute.id, pa.attribute.name, pa.attribute.dataType, "
            + "pa.attribute.unit.symbol, val "
            + "ORDER BY pa.attribute.name, val")
    List<Object[]> findFiltersForSubCategory(Long subCategoryId);

    // Те саме для product group
    @Query("SELECT pa.attribute.id, pa.attribute.name, pa.attribute.dataType, "
            + "pa.attribute.unit.symbol, "
            + "COALESCE(pa.optionValue.value, pa.customValue) AS val "
            + "FROM ProductAttribute pa "
            + "JOIN pa.product p "
            + "WHERE p.productGroup.id = :productGroupId "
            + "GROUP BY pa.attribute.id, pa.attribute.name, pa.attribute.dataType, "
            + "pa.attribute.unit.symbol, val "
            + "ORDER BY pa.attribute.name, val")
    List<Object[]> findFiltersForProductGroup(Long productGroupId);
}
