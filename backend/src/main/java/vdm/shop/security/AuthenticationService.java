package vdm.shop.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vdm.shop.dto.user.UserLoginRequestDto;
import vdm.shop.dto.user.UserLoginResponseDto;
import vdm.shop.model.Role;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        log.info("Authenticating user with email: {}", requestDto.email());
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.email(),
                            requestDto.password())
            );
            String token = jwtUtil.generateToken(authentication.getName());
            Long roleId = Role.RoleName.valueOf(authentication.getAuthorities().toArray()[0]
                    .toString()).ordinal() + 1L;
            log.info("Authentication successful for user: {}", requestDto.email());
            return new UserLoginResponseDto(token, roleId);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", requestDto.email(), e);
            throw e;
        }
    }
}
