package vdm.shop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email
        @NotBlank
        @Size(min = 8, max = 255)
        String email,
        @NotBlank
        @Size(min = 8, max = 255)
        String password
) {
}
