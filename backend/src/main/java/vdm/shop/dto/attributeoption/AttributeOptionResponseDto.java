package vdm.shop.dto.attributeoption;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeOptionResponseDto {
    private Long id;
    private Long attributeId;
    private String attributeName;
    private String value;
}
