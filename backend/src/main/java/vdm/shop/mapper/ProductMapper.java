package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.product.ProductRequestDto;
import vdm.shop.dto.product.ProductResponseDto;
import vdm.shop.model.Product;

@Mapper(config = MapperConfig.class)
public interface ProductMapper {
    @Mapping(target = "productGroup", ignore = true)
    Product toModel(ProductRequestDto requestDto);

    @Mapping(target = "productGroupId", source = "productGroup.id")
    ProductResponseDto toDto(Product product);

    @Mapping(target = "productGroupId", source = "productGroup.id")
    List<ProductResponseDto> toDtoList(List<Product> products);

    default Page<ProductResponseDto> toDtoPage(Page<Product> products) {
        List<ProductResponseDto> dtoList = products.map(this::toDto).getContent();
        return new PageImpl<>(dtoList, products.getPageable(), products.getTotalElements());
    }
}
