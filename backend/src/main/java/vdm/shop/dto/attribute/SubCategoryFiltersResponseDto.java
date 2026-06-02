package vdm.shop.dto.attribute;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vdm.shop.model.enumeration.AttributeDataType;

@Getter
@Setter
public class SubCategoryFiltersResponseDto {
    private Long subCategoryId;
    private List<FilterAttributeDto> filters;

    @Getter
    @Setter
    public static class FilterAttributeDto {
        private Long attributeId;
        private String attributeName;
        private AttributeDataType dataType;
        private String unitSymbol;
        // Для DICT — унікальні значення що реально є у товарах підкатегорії
        // Для NUMBER — min/max діапазон
        private List<String> values;
        private String minValue;
        private String maxValue;
    }
}
