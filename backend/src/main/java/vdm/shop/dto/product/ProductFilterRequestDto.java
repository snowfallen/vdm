package vdm.shop.dto.product;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequestDto {
    private Long subCategoryId;
    private Long productGroupId;
    private String name; // пошук по назві
    private List<AttributeFilterDto> attributes; // фільтри по атрибутах

    @Getter
    @Setter
    public static class AttributeFilterDto {
        private Long attributeId;
        private List<String> values; // OR між значеннями одного атрибуту
    }
}
