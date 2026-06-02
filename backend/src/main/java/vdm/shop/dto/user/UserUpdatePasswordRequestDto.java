package vdm.shop.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import vdm.shop.validation.FieldMatch;

@FieldMatch(
        field = "password",
        repeatField = "repeatPassword",
        message = "Password and repeat password should be equals"
)
@Setter
@Getter
public class UserUpdatePasswordRequestDto {
    @NotBlank
    @Size(min = 8, max = 255)
    private String password;
    @NotBlank
    @Size(min = 8, max = 255)
    private String repeatPassword;
}
