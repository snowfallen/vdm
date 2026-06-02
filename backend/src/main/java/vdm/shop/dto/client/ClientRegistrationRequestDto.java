package vdm.shop.dto.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vdm.shop.dto.user.UserRegistrationRequestDto;

@Getter
@Setter
public class ClientRegistrationRequestDto {
    @NotNull(message = "User registration data is required")
    @Valid
    private UserRegistrationRequestDto userRegistrationData;

    @NotNull(message = "Client data is required")
    @Valid
    private ClientRequestDto clientData;
}
