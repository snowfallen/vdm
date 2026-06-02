package vdm.shop.repository.specification.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vdm.shop.model.Product;
import vdm.shop.repository.specification.SpecificationProvider;
import vdm.shop.repository.specification.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class ProductSpecificationProviderManager
        implements SpecificationProviderManager<Product> {
    private static final String CANT_FIND_PROVIDER = "Can't find provider with key: ";
    private final List<SpecificationProvider<Product>> specificationProviders;

    @Override
    public SpecificationProvider<Product> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(CANT_FIND_PROVIDER + key));
    }
}
