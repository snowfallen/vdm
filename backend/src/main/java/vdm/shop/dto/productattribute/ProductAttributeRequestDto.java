package vdm.shop.dto.productattribute;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductAttributeRequestDto(
        @NotNull @Positive Long productId,
        @NotNull @Positive Long attributeId,
        Long optionId, // для DICT
        String customValue // для TEXT / NUMBER
) {}
