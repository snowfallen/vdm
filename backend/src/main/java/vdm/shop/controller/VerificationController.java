package vdm.shop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.repository.user.UserRepository;
import vdm.shop.service.token.VerificationService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Email verification")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;
    private final UserRepository userRepo;

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        verificationService.verify(token);
        return ResponseEntity.ok(
                "E-mail został potwierdzony. Możesz się zalogować.");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resend(@RequestParam String email) {
        var user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));
        if (user.isEmailVerified()) {
            return ResponseEntity.ok("Adres e-mail był już potwierdzony.");
        }
        verificationService.createTokenAndSend(user);
        return ResponseEntity.ok("Wysłano nowy link aktywacyjny.");
    }
}
