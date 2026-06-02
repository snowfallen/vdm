package vdm.shop.service.token;

import org.springframework.transaction.annotation.Transactional;
import vdm.shop.model.User;

public interface VerificationService {
    @Transactional
    void createTokenAndSend(User user);

    @Transactional
    void verify(String token);
}
