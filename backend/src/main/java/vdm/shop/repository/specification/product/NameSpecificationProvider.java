package vdm.shop.repository.specification.product;

import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vdm.shop.model.Product;
import vdm.shop.repository.specification.SpecificationProvider;

@Component
public class NameSpecificationProvider implements SpecificationProvider<Product> {
    private static final String FIELD_NAME = "name";

    @Override
    public String getKey() {
        return FIELD_NAME;
    }

    @Override
    public Specification<Product> getSpecification(String[] params) {
        if (params == null || params.length == 0) {
            return (root, query, cb) -> cb.conjunction();
        }

        List<String> names = Arrays.stream(params)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (names.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) ->
                cb.or(names.stream()
                        .map(name -> cb.like(
                                cb.lower(root.get(FIELD_NAME)),
                                "%" + name.toLowerCase() + "%"))
                        .toArray(Predicate[]::new));
    }
}
