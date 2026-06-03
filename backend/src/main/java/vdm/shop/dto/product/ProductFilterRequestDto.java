package vdm.shop.dto.product;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequestDto {
    // Скоп пошуку — одне з двох
    private Long subCategoryId;
    private Long productGroupId;

    // Пошук по назві (для search)
    private String name;

    // Фільтри по атрибутах: AND між різними атрибутами
    // OR між values одного атрибуту
    private List<AttributeFilterDto> attributes;

    @Getter
    @Setter
    public static class AttributeFilterDto {
        private Long attributeId;
        // Для DICT: список обраних значень (OR)
        // Для NUMBER: ["100", "500"] — [min, max] або просто одне значення
        private List<String> values;
        // Для NUMBER range — окремо min/max якщо хочемо range
        private String minValue;
        private String maxValue;
    }
}
