package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.user.UserRegistrationRequestDto;
import vdm.shop.dto.user.UserResponseDto;
import vdm.shop.dto.user.UserUpdatePasswordRequestDto;
import vdm.shop.dto.user.UserUpdateRequestDto;
import vdm.shop.exception.RegistrationException;
import vdm.shop.model.User;
import vdm.shop.service.user.UserService;

@Tag(name = "Users entity management", description = "Endpoints for users entity management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Admin added successfully!"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied!")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        log.info("Registering a new user with data: {}", requestDto);
        return userService.register(requestDto);
    }

    @Operation(summary = "Get list of all users",
            description = "Fetches a paginated list of all registered users. "
                    + "Only accessible by users with ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved list of users"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all users with pageable: {}", pageable);
        return userService.getAll(pageable);
    }

    @Operation(summary = "Get list of all accountants",
            description = "Fetches a paginated list of all accountant users. "
                    + "Only accessible by users with ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved list of accountants"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @GetMapping("accountants")
    @PreAuthorize("hasRole('ADMIN') || hasRole('ACCOUNTANT')")
    public List<UserResponseDto> getAllAccountant(Pageable pageable) {
        log.info("Fetching all accountants with pageable: {}", pageable);
        return userService.getAllAccountant(pageable);
    }

    @Operation(summary = "Get user by ID",
            description = "Fetches the details of a user by their unique ID. "
                    + "Only accessible by users with ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied"),
            @ApiResponse(responseCode = "404",
                    description = "User not found")
    })
    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id) {
        log.info("Fetching user by ID: {}", id);
        return userService.getUserById(id);
    }

    @Operation(summary = "Get personal info for authenticated user",
            description = "Fetches the personal account information "
                    + "for the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved personal info"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @GetMapping("/info")
    public UserResponseDto getPersonalInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Fetching personal info for user ID: {}", user.getId());
        return userService.getUserById(user.getId());
    }

    @Operation(summary = "Update user account by ID",
            description = "Updates the account details of a user by their unique ID."
                    + " Only accessible by users with ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully updated user"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied"),
            @ApiResponse(responseCode = "404",
                    description = "User not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto update(@RequestBody @Valid UserUpdateRequestDto requestDto,
                                  @PathVariable Long id) {
        log.info("Updating user with ID: {} and data: {}", id, requestDto);
        return userService.update(requestDto, id);
    }

    @Operation(summary = "Update user account password by ID",
            description = "Updates the password of a user by their unique ID. "
                    + "Only accessible by users with ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully updated user password"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied"),
            @ApiResponse(responseCode = "404",
                    description = "User not found")
    })
    @PutMapping("/password/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto updatePassword(
            @RequestBody @Valid UserUpdatePasswordRequestDto requestDto,
            @PathVariable Long id) {
        log.info("Updating password for user ID: {} with data: {}", id, requestDto);
        return userService.updatePassword(requestDto, id);
    }

    @Operation(summary = "Update own account password",
            description = "Allows the authenticated user to update their own account password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully updated own password"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @PutMapping("/password")
    public UserResponseDto updateOwnPassword(
            @RequestBody @Valid UserUpdatePasswordRequestDto requestDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Updating own password for user ID: {} with data: {}", user.getId(), requestDto);
        return userService.updatePassword(requestDto, user.getId());
    }

    @Operation(summary = "Update own account data",
            description = "Allows the authenticated user to update their own account information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully updated own data"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @PutMapping("/update")
    public UserResponseDto updateOwnData(@RequestBody @Valid UserUpdateRequestDto requestDto,
                                         Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Updating own data for user ID: {} with data: {}", user.getId(), requestDto);
        return userService.update(requestDto, user.getId());
    }

    @Operation(summary = "Delete user account by ID",
            description = "Deletes a user account by its unique ID. "
                    + "Only accessible by users with ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully deleted user"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied"),
            @ApiResponse(responseCode = "404",
                    description = "User not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto delete(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        return userService.delete(id);
    }
}
