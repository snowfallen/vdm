package vdm.shop.repository.specification.product;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vdm.shop.dto.product.ProductFilterRequestDto;
import vdm.shop.model.Product;
import vdm.shop.model.ProductAttribute;
import vdm.shop.repository.specification.SpecificationBuilder;
import vdm.shop.repository.specification.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class ProductSpecificationBuilder
        implements SpecificationBuilder<Product, ProductFilterRequestDto> {
    private final SpecificationProviderManager<Product> productSpecificationProviderManager;

    @Override
    public Specification<Product> build(ProductFilterRequestDto dto) {
        Specification<Product> spec = Specification.where(null);

        // Фільтр по підкатегорії
        if (dto.getSubCategoryId() != null) {
            spec = spec.and(productSpecificationProviderManager
                    .getSpecificationProvider("subCategory")
                    .getSpecification(new String[]{String.valueOf(dto.getSubCategoryId())}));
        }

        // Фільтр по product group
        if (dto.getProductGroupId() != null) {
            spec = spec.and(productSpecificationProviderManager
                    .getSpecificationProvider("productGroup")
                    .getSpecification(new String[]{String.valueOf(dto.getProductGroupId())}));
        }

        // Пошук по назві
        if (dto.getName() != null && !dto.getName().isBlank()) {
            spec = spec.and(productSpecificationProviderManager
                    .getSpecificationProvider("name")
                    .getSpecification(new String[]{dto.getName()}));
        }

        // Фільтри по атрибутах — AND між різними атрибутами
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            for (ProductFilterRequestDto.AttributeFilterDto attrFilter : dto.getAttributes()) {
                if (attrFilter.getValues() != null && !attrFilter.getValues().isEmpty()) {
                    spec = spec.and(buildAttributeSpec(
                            attrFilter.getAttributeId(),
                            attrFilter.getValues()
                    ));
                }
            }
        }

        return spec;
    }

    // OR між значеннями одного атрибуту
    private Specification<Product> buildAttributeSpec(Long attributeId, List<String> values) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Product, ProductAttribute> pa = root.join("productAttributes", JoinType.INNER);

            List<Predicate> valuePredicates = new ArrayList<>();
            for (String val : values) {
                Predicate byOption = cb.and(
                        cb.equal(pa.get("attribute").get("id"), attributeId),
                        cb.equal(pa.get("optionValue").get("value"), val)
                );
                Predicate byCustom = cb.and(
                        cb.equal(pa.get("attribute").get("id"), attributeId),
                        cb.equal(pa.get("customValue"), val)
                );
                valuePredicates.add(cb.or(byOption, byCustom));
            }

            return cb.and(
                    cb.equal(pa.get("attribute").get("id"), attributeId),
                    cb.or(valuePredicates.toArray(new Predicate[0]))
            );
        };
    }
}
