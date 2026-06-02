package vdm.shop.dto.productgroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductGroupResponseDto {
    private Long id;
    private String name;
    private Long subCategoryId;
}
