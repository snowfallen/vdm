package vdm.shop.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequestDto {
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must be less than 100 characters")
    private String country;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street must be less than 255 characters")
    private String street;

    @NotBlank(message = "House number is required")
    @Size(max = 20, message = "House number must be less than 20 characters")
    private String houseNumber;

    @Size(max = 20, message = "Apartment number must be less than 20 characters")
    private String apartmentNumber;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must be less than 20 characters")
    private String postalCode;
}
