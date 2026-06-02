package vdm.shop.service.email;

import jakarta.mail.MessagingException;
import vdm.shop.dto.email.MailRequestDto;

public interface MailService {
    void sendVerificationEmail(MailRequestDto mailRequestDto) throws MessagingException;
}
