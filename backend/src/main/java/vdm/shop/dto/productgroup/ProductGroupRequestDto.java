package vdm.shop.dto.productgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductGroupRequestDto(
        @NotBlank
        @Size(min = 2, max = 255)
        String name,
        @NotNull
        @Positive
        Long subCategoryId
) {}
