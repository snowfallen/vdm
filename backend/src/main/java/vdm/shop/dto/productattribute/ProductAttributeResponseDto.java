package vdm.shop.dto.productattribute;

import lombok.Getter;
import lombok.Setter;
import vdm.shop.model.enumeration.AttributeDataType;

@Getter
@Setter
public class ProductAttributeResponseDto {
    private Long id;
    private Long productId;
    private Long attributeId;
    private String attributeName;
    private AttributeDataType dataType;
    private String unitSymbol;
    private Long optionId;
    private String value; // optionValue.value або customValue — злите для зручності
}
