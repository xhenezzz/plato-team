package aidyn.kelbetov.repo;

import aidyn.kelbetov.dto.EmailChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {
    Optional<EmailChangeToken> findByToken(String token);
}
