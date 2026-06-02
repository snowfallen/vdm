package vdm.shop.dto.attribute;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vdm.shop.model.enumeration.AttributeDataType;

@Getter
@Setter
public class AttributeWithOptionsResponseDto {
    private Long id;
    private String name;
    private AttributeDataType dataType;
    private String unitSymbol;
    private List<OptionDto> options; // заповнено тільки для DICT

    @Getter
    @Setter
    public static class OptionDto {
        private Long id;
        private String value;
    }
}
