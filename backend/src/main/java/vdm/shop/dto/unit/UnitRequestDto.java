package vdm.shop.dto.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnitRequestDto(
        @NotBlank @Size(min = 1, max = 20)
        String symbol,
        String description
) {}
