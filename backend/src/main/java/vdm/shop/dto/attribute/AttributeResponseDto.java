package vdm.shop.dto.attribute;

import lombok.Getter;
import lombok.Setter;
import vdm.shop.model.enumeration.AttributeDataType;

@Getter
@Setter
public class AttributeResponseDto {
    private Long id;
    private String name;
    private AttributeDataType dataType;
    private Long unitId;
    private String unitSymbol; // зручно для фронту щоб не робити окремий запит
}
