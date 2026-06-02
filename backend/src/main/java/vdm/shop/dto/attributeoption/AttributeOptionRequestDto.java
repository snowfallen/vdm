package vdm.shop.dto.attributeoption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AttributeOptionRequestDto(
        @NotNull @Positive
        Long attributeId,
        @NotBlank @Size(min = 1, max = 255)
        String value
) {}
