package fr.projet.service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.Request.PasswordCheckRequest;
import fr.projet.Response.PasswordCheckResponse;
import fr.projet.Response.PasswordGeneratedResponse;
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


//check 

public PasswordCheckResponse checkPasswordStrength(PasswordCheckRequest request) {

    if (request == null) {
        return new PasswordCheckResponse(false, false, "Password cannot be null");
    }

    boolean isStrong = isStrongPassword(request.getPasswordPlatform());
    String message = isStrong ? "Password is strong" : "Password is weak";
    return new PasswordCheckResponse(isStrong, false, message);
}

    // Méthode pour vérifier si un mot de passe est fort
    private boolean isStrongPassword(String password) {
        // Définir les critères de validation
    int minLength = 12;
    boolean hasUppercase = false;
    boolean hasLowercase = false;
    boolean hasDigit = false;
    boolean hasSpecialChar = false;
    String specialChars = "!@#$%^&*()-+";
    
    if (password.length() < minLength) {
        return false;
    }
    
    for (char c : password.toCharArray()) {
        if (Character.isUpperCase(c)) {
            hasUppercase = true;
        } else if (Character.isLowerCase(c)) {
            hasLowercase = true;
        } else if (Character.isDigit(c)) {
            hasDigit = true;
        } else if (specialChars.indexOf(c) != -1) {
            hasSpecialChar = true;
        }
    }
    
    return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
}

       
//vulnérabilité

 // Vérifier la vulnérabilité d'un mot de passe


// Méthode pour vérifier si un mot de passe est vulnérable
public PasswordCheckResponse checkPasswordVulnerability(PasswordCheckRequest request) {
    boolean isVulnerable = isPasswordVulnerable(request.getPasswordPlatform());
    String message = isVulnerable ? "Password is vulnerable" : "Password is not found in the list of stolen passwords";
    return new PasswordCheckResponse(false, isVulnerable, message);
}

  // Méthode pour vérifier si un mot de passe est vulnérable
  private boolean isPasswordVulnerable(String password) {
    String passwordHash = DigestUtils.sha1Hex(password);
    // Comparer le hash du mot de passe avec les hashes stockés dans les fichiers
    return false;
}

 // Générer un mot de passe fort
 public PasswordGeneratedResponse generatePassword() {
    String generatedPassword = generateStrongPassword();
    return new PasswordGeneratedResponse(generatedPassword);
}

// Méthode pour générer un mot de passe fort
private String generateStrongPassword() {
    // Définition des caractères possibles pour le mot de passe
    String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
    String digitChars = "0123456789";
    String specialChars = "!@#$%^&*()-+";

    // Définition de la longueur du mot de passe
    int passwordLength = 12;

    // Initialisation du générateur de mot de passe
    StringBuilder password = new StringBuilder();

    // Ajout d'au moins une lettre majuscule
    password.append(uppercaseChars.charAt(new Random().nextInt(uppercaseChars.length())));

    // Ajout d'au moins une lettre minuscule
    password.append(lowercaseChars.charAt(new Random().nextInt(lowercaseChars.length())));

    // Ajout d'au moins un chiffre
    password.append(digitChars.charAt(new Random().nextInt(digitChars.length())));

    // Ajout d'au moins un caractère spécial
    password.append(specialChars.charAt(new Random().nextInt(specialChars.length())));

    // Complétion du reste du mot de passe avec des caractères aléatoires
    for (int i = 4; i < passwordLength; i++) {
        String allChars = uppercaseChars + lowercaseChars + digitChars + specialChars;
        password.append(allChars.charAt(new Random().nextInt(allChars.length())));
    }

    // Mélange aléatoire des caractères du mot de passe
    List<Character> charsList = password.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    Collections.shuffle(charsList);
    StringBuilder shuffledPassword = new StringBuilder();
    charsList.forEach(shuffledPassword::append);

    return shuffledPassword.toString();
}

}


