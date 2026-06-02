package vdm.shop.repository.specification;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T, V> {
    Specification<T> build(V searchParametersDto);
}
