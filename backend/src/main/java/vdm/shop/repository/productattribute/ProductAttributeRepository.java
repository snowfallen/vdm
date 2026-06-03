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

    // ВАЖЛИВО: pa.attribute.unit може бути null для DICT/TEXT атрибутів.
    // Тому використовуємо явний LEFT JOIN FETCH через нативний підхід:
    // вибираємо unit окремо через LEFT JOIN щоб уникнути NPE при null unit.
    @Query("SELECT pa.attribute.id, pa.attribute.name, "
            + "CAST(pa.attribute.dataType AS string), "
            + "u.symbol, "
            + "COALESCE(ov.value, pa.customValue) "
            + "FROM ProductAttribute pa "
            + "JOIN pa.product p "
            + "JOIN p.productGroup pg "
            + "LEFT JOIN pa.attribute.unit u "
            + "LEFT JOIN pa.optionValue ov "
            + "WHERE pg.subCategory.id = :subCategoryId "
            + "GROUP BY pa.attribute.id, pa.attribute.name, pa.attribute.dataType, "
            + "u.symbol, COALESCE(ov.value, pa.customValue) "
            + "ORDER BY pa.attribute.name, COALESCE(ov.value, pa.customValue)")
    List<Object[]> findFiltersForSubCategory(Long subCategoryId);

    @Query("SELECT pa.attribute.id, pa.attribute.name, "
            + "CAST(pa.attribute.dataType AS string), "
            + "u.symbol, "
            + "COALESCE(ov.value, pa.customValue) "
            + "FROM ProductAttribute pa "
            + "JOIN pa.product p "
            + "LEFT JOIN pa.attribute.unit u "
            + "LEFT JOIN pa.optionValue ov "
            + "WHERE p.productGroup.id = :productGroupId "
            + "GROUP BY pa.attribute.id, pa.attribute.name, pa.attribute.dataType, "
            + "u.symbol, COALESCE(ov.value, pa.customValue) "
            + "ORDER BY pa.attribute.name, COALESCE(ov.value, pa.customValue)")
    List<Object[]> findFiltersForProductGroup(Long productGroupId);
}
