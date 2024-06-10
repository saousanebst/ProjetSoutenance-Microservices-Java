package fr.projet.service;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.projet.model.Password;
import fr.projet.model.PasswordResetToken;
import fr.projet.repository.PasswordRepository;
import fr.projet.repository.PasswordResetTokenRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
@Service
public class PasswordService {
    
@Autowired
private PasswordRepository passwordRepository;

// @Autowired
// private UserFeignClient userFeignClient; // Utilisation d'un client Feign pour obtenir des informations utilisateur


@Autowired
private PasswordResetTokenRepository passwordResetTokenRepository;



//create password

    public Password createPassword(Password password) {
        password.setDateAjout(LocalDateTime.now());
        this.passwordRepository.save(password);

        return password;

    }


//update 

 

    public void updatePassword(String idUser, String newPassword) {
        Optional<Password> optionalPassword = passwordRepository.findById(idUser);
        if (optionalPassword.isPresent()) {
            Password password = optionalPassword.get();
            password.setPasswordValue(hashPassword(newPassword));
            password.setDateModif(LocalDateTime.now());
            passwordRepository.save(password);

            
        } else {
            throw new RuntimeException("User not found");
        }
    }


    public String getPasswordByUserId(String idUser) {
        Password passwordValue = passwordRepository.findByidUser(idUser);
        if (passwordValue != null) {
            return passwordValue.getPasswordValue();
        } else {
            return null;
        }
    }





// Injectez les dépendances nécessaires, par exemple un référentiel de mots de passe compromis

    // public PasswordCheckResponse checkPassword(PasswordCheckRequest request) {
    //     // Logique de vérification du mot de passe
    //     boolean isVulnerable = checkPasswordVulnerability(request.getPassword());
    //     String message = isVulnerable ? "Password is vulnerable" : "Password is safe";
    //     return new PasswordCheckResponse(isVulnerable, message);
    // }

    // public PasswordGenerateResponse generatePassword() {
    //     // Logique de génération de mot de passe
    //     String generatedPassword = generateStrongPassword();
    //     String strength = evaluatePasswordStrength(generatedPassword);
    //     return new PasswordGenerateResponse(generatedPassword, strength);
    // }

    // private boolean checkPasswordVulnerability(String password) {
    //     // Logique pour vérifier si le mot de passe est compromis
    //     // Par exemple, comparer le hash du mot de passe avec une base de données de mots de passe compromis
    //     return false; // Modifier selon la logique réelle
    // }

    // private String generateStrongPassword() {
    //     // Logique pour générer un mot de passe fort
    //     return "GeneratedPassword123!";
    // }

    // private String evaluatePasswordStrength(String password) {
    //     // Logique pour évaluer la force du mot de passe
 
 
    //     return "Strong";
    // }



//reset request 
    public void requestPasswordReset(String email) {
        // Logique pour demander la réinitialisation du mot de passe
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, email);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "https://example.com/reset-password?token=" + token;
        System.out.println("Email sent to " + email + " with reset link: " + resetLink);
    }


    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> optionalResetToken = passwordResetTokenRepository.findByToken(token);
        if (!optionalResetToken.isPresent()) {
        throw new RuntimeException("Invalid token");
        }
        
        PasswordResetToken resetToken = optionalResetToken.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
        throw new RuntimeException("Token has expired");
        }
        
        Optional<Password> optionalPassword = passwordRepository.findById(resetToken.getEmail());
        if (!optionalPassword.isPresent()) {
        throw new RuntimeException("Email User not found");
        }
        
        Password password = optionalPassword.get();
        password.setPasswordValue(hashPassword(newPassword));
        password.setDateModif(LocalDateTime.now());
        passwordRepository.save(password);
        }
        
        private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
        }





}



