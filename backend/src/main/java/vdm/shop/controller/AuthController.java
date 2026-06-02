package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.user.UserLoginRequestDto;
import vdm.shop.dto.user.UserLoginResponseDto;
import vdm.shop.security.AuthenticationService;

@Tag(name = "Auth management", description = "Endpoints for authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Login",
            description = "Authenticates user credentials and returns "
                    + "a JWT token upon successful login."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Login successful. JWT token returned"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Validation errors"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Invalid credentials"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        log.info("Received login request for email: {}", request.email());
        try {
            UserLoginResponseDto responseDto = authenticationService.authenticate(request);
            log.info("Login successful for email: {}", request.email());
            return responseDto;
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.email(), e);
            throw e;
        }
    }
}
