package vdm.shop.dto.user;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long roleId;
    private String email;
    private String phoneNumber;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
