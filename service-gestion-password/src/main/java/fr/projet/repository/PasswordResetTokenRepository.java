package fr.projet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String>{

    Optional<PasswordResetToken> findByToken(String token);
    
}
