package vdm.shop.dto.client;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponseDto {
    private Long id;
    private Long userId;
    private String country;
    private String city;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private String postalCode;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
