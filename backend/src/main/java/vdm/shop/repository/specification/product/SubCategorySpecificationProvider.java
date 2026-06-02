package vdm.shop.repository.specification.product;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vdm.shop.model.Product;
import vdm.shop.repository.specification.SpecificationProvider;

@Component
public class SubCategorySpecificationProvider implements SpecificationProvider<Product> {
    private static final String KEY = "subCategory";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Product> getSpecification(String[] params) {
        if (params == null || params.length == 0) {
            return (root, query, cb) -> cb.conjunction();
        }

        List<Long> ids = Arrays.stream(params)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .toList();

        return (root, query, cb) ->
                root.get("productGroup").get("subCategory").get("id").in(ids);
    }
}
