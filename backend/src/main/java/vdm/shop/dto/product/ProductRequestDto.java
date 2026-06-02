package vdm.shop.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank
        @Size(min = 2, max = 255)
        String name,
        @NotNull
        @Positive
        BigDecimal price,
        @NotNull
        @Positive
        Long productGroupId
) {}
