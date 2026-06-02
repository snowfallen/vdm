package vdm.shop.dto.attribute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import vdm.shop.model.enumeration.AttributeDataType;

public record AttributeRequestDto(
        @NotBlank @Size(min = 2, max = 255)
        String name,
        @NotNull
        AttributeDataType dataType,
        Long unitId // nullable — тільки для NUMBER
) {}
