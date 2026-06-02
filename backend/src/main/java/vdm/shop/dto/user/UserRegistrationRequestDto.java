package vdm.shop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import vdm.shop.validation.FieldMatch;

@FieldMatch(
        field = "password",
        repeatField = "repeatPassword",
        message = "Password and repeat password should be equals"
)
@Getter
@Setter
public class UserRegistrationRequestDto {
    @Email
    @NotBlank
    @Size(min = 8, max = 255)
    private String email;
    @NotBlank
    private String phoneNumber;
    @Positive
    @NotNull
    private Long roleId;
    @NotBlank
    @Size(min = 2, max = 255)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 255)
    private String lastName;
    @NotBlank
    @Size(min = 8, max = 255)
    private String password;
    @NotBlank
    @Size(min = 8, max = 255)
    private String repeatPassword;
}
