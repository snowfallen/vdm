package vdm.shop.dto.subcategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryResponseDto {
    private Long id;
    private String name;
    private Long categoryId;
}
