package vdm.shop.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailRequestDto {

    private String clientEmail;
    private String emailSubject;
    private String question;
    private String clientName;
    private String clientPhoneNumber;

    public MailRequestDto(String email, String subject, String htmlBody) {
        this.clientEmail = email;
        this.emailSubject = subject;
        this.question = htmlBody;
    }
}
