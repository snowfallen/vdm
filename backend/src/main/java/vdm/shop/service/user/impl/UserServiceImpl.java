package vdm.shop.service.user.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vdm.shop.dto.user.UserRegistrationRequestDto;
import vdm.shop.dto.user.UserResponseDto;
import vdm.shop.dto.user.UserUpdatePasswordRequestDto;
import vdm.shop.dto.user.UserUpdateRequestDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.exception.RegistrationException;
import vdm.shop.mapper.UserMapper;
import vdm.shop.model.Role;
import vdm.shop.model.User;
import vdm.shop.repository.role.RoleRepository;
import vdm.shop.repository.user.UserRepository;
import vdm.shop.service.token.VerificationService;
import vdm.shop.service.user.UserService;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String CANT_FIND_ROLE_BY_NAME = "Can't find role by id";
    private static final String USER_NOW_FOUND_BY_ID = "User not found by id: ";
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        log.info("Registering new user with email: {}", requestDto.getEmail());
        return userMapper.toDto(createUser(requestDto));
    }

    @Override
    public User createUser(UserRegistrationRequestDto requestDto) {
        log.info("Creating user account for email: {}", requestDto.getEmail());
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(setUserRoleSet(requestDto.getRoleId()));
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        verificationService.createTokenAndSend(savedUser);
        log.info("User account created successfully for email: {}", requestDto.getEmail());
        return savedUser;
    }

    @Override
    public Page<UserResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all users with pagination.");
        return userMapper.toDtoPage(userRepository.findAllNotClient(pageable));
    }

    @Override
    public List<UserResponseDto> getAllAccountant(Pageable pageable) {
        log.info("Fetching all accountants with pagination.");
        return userMapper.toDtoList(userRepository.findAllAccountant(pageable));
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        return userMapper.toDto(getUser(id));
    }

    @Override
    public UserResponseDto update(UserUpdateRequestDto requestDto, Long id) {
        log.info("Updating user with ID: {}", id);
        User user = getUser(id);
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setModifiedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        log.info("User with ID: {} updated successfully.", id);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto updatePassword(UserUpdatePasswordRequestDto requestDto, Long id) {
        log.info("Updating password for user with ID: {}", id);
        User user = getUser(id);
        user.setModifiedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User updatedUser = userRepository.save(user);
        log.info("Password updated successfully for user with ID: {}", id);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @Override
    public UserResponseDto delete(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = getUser(id);
        UserResponseDto deletedUserDto = userMapper.toDto(user);

        userRepository.delete(user);
        log.info("User with ID: {} deleted. Associated records (Client, Orders, Cart) "
                + "should be deleted by database cascade.", id);

        return deletedUserDto;
    }

    private User getUser(Long id) {
        log.debug("Retrieving user with ID: {}", id);
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(USER_NOW_FOUND_BY_ID + id)
        );
    }

    private Set<Role> setUserRoleSet(Long roleId) {
        log.debug("Setting roles for user with role ID: {}", roleId);
        Role userRole = roleRepository.findRoleById(roleId)
                .orElseThrow(() -> new RuntimeException(CANT_FIND_ROLE_BY_NAME));
        Set<Role> userRoleSet = new HashSet<>();
        userRoleSet.add(userRole);
        log.debug("Roles set for user with role ID: {}", roleId);
        return userRoleSet;
    }
}
