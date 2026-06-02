package vdm.shop.repository.token;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vdm.shop.model.VerificationToken;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
