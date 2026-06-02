package vdm.shop.service.email.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vdm.shop.dto.email.MailRequestDto;
import vdm.shop.service.email.MailService;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private static final String ADMIN_EMAIL = "bondarenkoov.dev@gmail.com";

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendVerificationEmail(MailRequestDto mailRequestDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(sender);
        helper.setTo(mailRequestDto.getClientEmail());
        helper.setSubject(mailRequestDto.getEmailSubject());
        helper.setText(createEmailVerificationContent(mailRequestDto), true);

        mailSender.send(message);
    }

    private String createEmailContent(MailRequestDto mail) {
        return """
        <!doctype html>
        <html lang="pl">
        <head>
          <meta charset="UTF-8">
          <title>%s</title>
        </head>
        <body style="margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif;background:#f5f6fa;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f5f6fa;padding:25px 0;">
            <tr>
              <td align="center">
                <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:8px;overflow:hidden;">
                  <tr>
                    <td style="padding:25px 30px;background:#001961;color:#ffffff;font-size:24px;font-weight:600;">
                      Danzig&nbsp;Carpets
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:25px 30px;">
                      <h2 style="margin-top:0;color:#333;font-weight:600;">📩 %s</h2>

                      <table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;font-size:14px;margin-bottom:20px;">
                        <tr>
                          <td style="padding:8px;border:1px solid #e0e0e0;font-weight:600;width:40%%;">📧 Email</td>
                          <td style="padding:8px;border:1px solid #e0e0e0;">%s</td>
                        </tr>
                        <tr>
                          <td style="padding:8px;border:1px solid #e0e0e0;font-weight:600;">🧑‍💼 Imię i nazwisko</td>
                          <td style="padding:8px;border:1px solid #e0e0e0;">%s</td>
                        </tr>
                        <tr>
                          <td style="padding:8px;border:1px solid #e0e0e0;font-weight:600;">📞 Numer telefonu</td>
                          <td style="padding:8px;border:1px solid #e0e0e0;">%s</td>
                        </tr>
                      </table>

                      <h3 style="margin:0 0 10px 0;font-size:16px;color:#333;font-weight:600;">📝 Treść zapytania:</h3>
                      <p style="font-size:14px;color:#333;white-space:pre-line;">%s</p>

                      <p style="font-size:13px;color:#777;margin-top:30px;">Wiadomość wygenerowana automatycznie – nie odpowiadaj na ten e-mail.</p>
                    </td>
                  </tr>
                  <tr>
                    <td style="background:#001961;color:#ffffff;text-align:center;padding:15px;font-size:12px;">
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
                mail.getEmailSubject(),
                mail.getEmailSubject(),
                mail.getClientEmail(),
                mail.getClientName(),
                mail.getClientPhoneNumber(),
                mail.getQuestion(),
                LocalDateTime.now().getYear()
        );
    }

    private String createEmailVerificationContent(MailRequestDto mailRequestDto) {
        return """
                <html>
                <body>
                    <hr>
                    <p>%s</p>
                </body>
                </html>
                """.formatted(
                mailRequestDto.getQuestion()
        );
    }
}
