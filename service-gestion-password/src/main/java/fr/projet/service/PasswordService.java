package fr.projet.service;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;



import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.projet.DTO.UtilisateurDto;
import fr.projet.OpenFeign.UserServiceClient;
import fr.projet.Request.PasswordCheckRequest;
import fr.projet.Response.PasswordCheckResponse;
import fr.projet.Response.PasswordGeneratedResponse;
import fr.projet.model.Password;
import fr.projet.model.PasswordResetToken;
import fr.projet.model.ResetPasswordException;
import fr.projet.repository.PasswordRepository;
import fr.projet.repository.PasswordResetTokenRepository;
import jakarta.mail.internet.MimeMessage;

import org.springframework.security.crypto.bcrypt.BCrypt;
@Service
@Transactional
public class PasswordService {
    
@Autowired
private PasswordRepository passwordRepository;

 @Autowired
    private UserServiceClient userServiceClient;
@Autowired
private JavaMailSender javaMailSender;


@Autowired
private PasswordResetTokenRepository passwordResetTokenRepository;

 @Autowired
private JdbcTemplate jdbcTemplate;

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

    public void requestPasswordReset(String email) {
        // Génération du lien de réinitialisation
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, email);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); 
        passwordResetTokenRepository.save(resetToken);

        // Envoi de l'email réel via JavaMail
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setText("To reset your password, click the link below:\n\n" +
                           "http://localhost:4200/reset-password?token=" + token);
            javaMailSender.send(message);

            System.out.println("Email sent to " + email + " with reset link.");
        } catch (Exception e) {
            // Gestion des erreurs d'envoi d'email
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email.");
        }
    }



    @Transactional
public void resetPassword(String token, String newPassword) {
    // Récupérer le token de réinitialisation
    Optional<PasswordResetToken> optionalResetToken = passwordResetTokenRepository.findByToken(token);
    
    if (!optionalResetToken.isPresent()) {
        throw new ResetPasswordException("Invalid token");
    }
    
    PasswordResetToken resetToken = optionalResetToken.get();
    
    // Vérifier si le token a expiré
    if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
        throw new ResetPasswordException("Token has expired");
    }
    
    // Utiliser le Feign Client pour récupérer les informations de l'utilisateur
    UtilisateurDto utilisateurDTO = userServiceClient.getUserByEmail(resetToken.getEmail());
    
    if (utilisateurDTO == null) {
        throw new ResetPasswordException("User with email not found");
    }
    
    // Hash le nouveau mot de passe
    String hashedPassword = hashPassword(newPassword);
    
    // Mettre à jour le mot de passe dans le service utilisateur via Feign Client
    userServiceClient.updateUserPassword(utilisateurDTO.getId(), hashedPassword);
    
    // Supprimer le token de réinitialisation après utilisation
    // passwordResetTokenRepository.delete(resetToken);
}

String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
}



//check 

public PasswordCheckResponse checkPasswordStrength(PasswordCheckRequest request) {

    if (request == null) {
        return new PasswordCheckResponse(false, false, "Password cannot be null");
    }

    boolean isStrong = isStrongPassword(request.getPassword());
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
    // Appelle la méthode isPasswordVulnerable pour déterminer si le mot de passe est vulnérable
    boolean isVulnerable = isPasswordVulnerable(request.getPassword());
    // Crée un message en fonction du résultat de la vérification de la vulnérabilité
    String message = isVulnerable ? "Password is vulnerable" : "Password is not found in the list of stolen passwords";
    // Retourne une réponse indiquant si le mot de passe est vulnérable ou non
    return new PasswordCheckResponse(false, isVulnerable, message);
}

// Méthode pour vérifier si un mot de passe est vulnérable
boolean isPasswordVulnerable(String password) {
    // Hash du mot de passe fourni par l'utilisateur
   String passwordHash = DigestUtils.sha1Hex(password);
    // Requête SQL pour compter le nombre d'occurrences du hash du mot de passe dans la table stolen_passwords
    String sql = "SELECT COUNT(*) FROM stolen_password WHERE hash = ?";
    // Exécute la requête et récupère le résultat (nombre d'occurrences)
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, passwordHash);
    // Si le nombre d'occurrences est supérieur à 0, le mot de passe est considéré comme vulnérable
    return count != null && count > 0;
}

 // Générer un mot de passe fort
 public PasswordGeneratedResponse generatePassword() {
    String generatedPassword = generateStrongPassword();
    return new PasswordGeneratedResponse(generatedPassword);
}

// Méthode pour générer un mot de passe fort
String generateStrongPassword() {
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


