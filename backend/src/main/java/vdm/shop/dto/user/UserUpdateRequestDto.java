package vdm.shop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateRequestDto {
    @Email
    @NotBlank
    @Size(min = 8, max = 255)
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    @Size(min = 2, max = 255)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 255)
    private String lastName;
}
