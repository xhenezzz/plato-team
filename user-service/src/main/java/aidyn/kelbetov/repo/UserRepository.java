package aidyn.kelbetov.repo;

import aidyn.kelbetov.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
    Optional<User> findByConfirmationToken(String token);
    boolean existsByEmail(String email);
}
