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

        if (dto.getSubCategoryId() != null) {
            spec = spec.and(productSpecificationProviderManager
                    .getSpecificationProvider("subCategory")
                    .getSpecification(new String[]{String.valueOf(dto.getSubCategoryId())}));
        }

        if (dto.getProductGroupId() != null) {
            spec = spec.and(productSpecificationProviderManager
                    .getSpecificationProvider("productGroup")
                    .getSpecification(new String[]{String.valueOf(dto.getProductGroupId())}));
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            spec = spec.and(productSpecificationProviderManager
                    .getSpecificationProvider("name")
                    .getSpecification(new String[]{dto.getName()}));
        }

        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            for (ProductFilterRequestDto.AttributeFilterDto attrFilter : dto.getAttributes()) {
                Long attrId = attrFilter.getAttributeId();

                if (attrFilter.getMinValue() != null || attrFilter.getMaxValue() != null) {
                    spec = spec.and(buildNumberRangeSpec(
                            attrId,
                            attrFilter.getMinValue(),
                            attrFilter.getMaxValue()
                    ));
                } else if (attrFilter.getValues() != null && !attrFilter.getValues().isEmpty()) {
                    spec = spec.and(buildAttributeValuesSpec(attrId, attrFilter.getValues()));
                }
            }
        }

        return spec;
    }

    // OR між значеннями одного DICT атрибуту
    // Поле "productAttributes" — відповідає @OneToMany в Product.java
    private Specification<Product> buildAttributeValuesSpec(Long attributeId,
                                                            List<String> values) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);

            // productAttributes — ім'я поля в Product.java
            Join<Product, ProductAttribute> pa =
                    root.join("productAttributes", JoinType.INNER);

            List<Predicate> valuePredicates = new ArrayList<>();
            for (String val : values) {
                // Спробуємо через optionValue.value (для DICT)
                Predicate byOption;
                try {
                    byOption = cb.and(
                            cb.equal(pa.get("attribute").get("id"), attributeId),
                            cb.equal(pa.get("optionValue").get("value"), val)
                    );
                } catch (Exception e) {
                    byOption = cb.disjunction(); // якщо optionValue null — пропускаємо
                }

                // Через customValue (для TEXT/NUMBER)
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

    // NUMBER range — customValue між min і max
    private Specification<Product> buildNumberRangeSpec(Long attributeId,
                                                        String minStr,
                                                        String maxStr) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);

            Join<Product, ProductAttribute> pa =
                    root.join("productAttributes", JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(pa.get("attribute").get("id"), attributeId));
            predicates.add(cb.isNotNull(pa.get("customValue")));

            if (minStr != null && !minStr.isBlank()) {
                try {
                    double min = Double.parseDouble(minStr);
                    predicates.add(cb.greaterThanOrEqualTo(
                            pa.get("customValue").as(Double.class), min));
                } catch (NumberFormatException ignored) {
                }
            }
            if (maxStr != null && !maxStr.isBlank()) {
                try {
                    double max = Double.parseDouble(maxStr);
                    predicates.add(cb.lessThanOrEqualTo(
                            pa.get("customValue").as(Double.class), max));
                } catch (NumberFormatException ignored) {
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
