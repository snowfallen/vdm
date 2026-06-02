package vdm.shop.service.token.impl;

import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdm.shop.dto.email.MailRequestDto;
import vdm.shop.model.User;
import vdm.shop.model.VerificationToken;
import vdm.shop.repository.token.VerificationTokenRepository;
import vdm.shop.service.email.MailService;
import vdm.shop.service.token.VerificationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final VerificationTokenRepository tokenRepo;
    private final MailService mailService;
    String verifyUrl = "http://localhost:4200/verify-email";


    @Override
    @Transactional
    public void createTokenAndSend(User user) {
        tokenRepo.deleteByUserId(user.getId());
        VerificationToken vt = tokenRepo.save(VerificationToken.create(user, 24));
        sendVerificationEmail(user, vt.getToken());
        log.info("Verification e-mail sent to {}", user.getEmail());
    }

    @Override
    @Transactional
    public void verify(String rawToken) {
        System.out.println("======================================");
        System.out.println(rawToken);
        VerificationToken vt = tokenRepo.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (vt.isExpired()) {
            throw new IllegalStateException("Verification token expired");
        }

        vt.getUser().setEmailVerified(true);
        tokenRepo.delete(vt);
        log.info("E-mail {} verified successfully", vt.getUser().getEmail());
    }

    private void sendVerificationEmail(User user, String token) {
        String link = verifyUrl + "?token=" + token;
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);

        String html = buildVerificationHtml(user, link, expiry);

        try {
            mailService.sendVerificationEmail(new MailRequestDto(
                    user.getEmail(),
                    "Aktywacja konta – Danzig Carpets",
                    html
            ));
        } catch (MessagingException ex) {
            log.error("Failed to send verification e-mail to {}", user.getEmail(), ex);
            throw new RuntimeException(ex);
        }
    }

    private String buildVerificationHtml(User user, String link, LocalDateTime expiresAt) {
        return """
    <!doctype html>
    <html lang="pl">
    <head>
      <meta charset="UTF-8">
      <title>Aktywacja konta – Danzig Carpets</title>
    </head>
    <body style="margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif;background:#f5f6fa;">
      <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f5f6fa;padding:25px 0;">
        <tr>
          <td align="center">
            <table width="600" cellpadding="0" cellspacing="0"
                   style="background:#ffffff;border-radius:8px;overflow:hidden;">
              <tr>
                <td style="padding:25px 30px;background:#001961;color:#ffffff;font-size:24px;font-weight:600;">
                  Danzig&nbsp;Carpets
                </td>
              </tr>

              <tr>
                <td style="padding:25px 30px;">
                  <h2 style="margin-top:0;color:#333;font-weight:600;">
                    Dzień dobry %s!
                  </h2>

                  <p style="font-size:15px;color:#333;margin:0 0 18px 0;">
                    Dziękujemy za rejestrację. Aby aktywować konto, kliknij przycisk poniżej:
                  </p>

                  <p style="margin:30px 0;">
                    <a href="%s"
                       style="background:#001961;color:#ffffff;padding:12px 24px;
                              text-decoration:none;border-radius:6px;font-size:16px;font-weight:600;">
                      Potwierdź adres&nbsp;e-mail
                    </a>
                  </p>

                  <p style="font-size:14px;color:#666;margin:0 0 25px 0;">
                    Link pozostanie aktywny do %s.
                  </p>

                  <p style="font-size:12px;color:#999;margin:0;">
                    Jeśli to nie Ty zakładałeś konto, zignoruj tę wiadomość.
                  </p>
                </td>
              </tr>

              <tr>
                <td style="background:#001961;color:#ffffff;text-align:center;
                           padding:15px;font-size:12px;">
                  © %d Danzig Carpets – wszystkie prawa zastrzeżone
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </body>
    </html>
    """.formatted(
                user.getFirstName(),
                link,
                expiresAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                LocalDateTime.now().getYear()
        );
    }

    private String vtExpiryInfo(String token) {
        return tokenRepo.findByToken(token)
                .map(t -> t.getExpiresAt()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .orElse("-");
    }
}
