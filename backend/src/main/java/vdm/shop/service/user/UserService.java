package vdm.shop.service.user;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.user.UserRegistrationRequestDto;
import vdm.shop.dto.user.UserResponseDto;
import vdm.shop.dto.user.UserUpdatePasswordRequestDto;
import vdm.shop.dto.user.UserUpdateRequestDto;
import vdm.shop.exception.RegistrationException;
import vdm.shop.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    User createUser(UserRegistrationRequestDto requestDto);

    Page<UserResponseDto> getAll(Pageable pageable);

    List<UserResponseDto> getAllAccountant(Pageable pageable);

    UserResponseDto getUserById(Long id);

    UserResponseDto update(UserUpdateRequestDto requestDto, Long id);

    UserResponseDto updatePassword(UserUpdatePasswordRequestDto requestDto, Long id);

    UserResponseDto delete(Long id);
}
