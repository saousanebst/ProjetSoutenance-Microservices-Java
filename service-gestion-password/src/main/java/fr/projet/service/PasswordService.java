package fr.projet.service;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.OpenFeignClient.UserFeignClient;
import fr.projet.Response.UtilisateurResponse;
import fr.projet.model.Password;
import fr.projet.model.PasswordResetToken;
import fr.projet.repository.PasswordRepository;
import fr.projet.repository.PasswordResetTokenRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
@Service
public class PasswordService {
    
@Autowired
private PasswordRepository passwordRepository;

@Autowired
private UserFeignClient userFeignClient; // Utilisation d'un client Feign pour obtenir des informations utilisateur


@Autowired
private PasswordResetTokenRepository passwordResetTokenRepository;



 // obtenir un mot de passe par ID
 public Password getPasswordById(String id) {
    Optional<Password> optionalPassword = passwordRepository.findById(id);
    return optionalPassword.orElse(null);
}

//create password

    public Password createPassword(Password password) {
        password.setDateAjout(LocalDateTime.now());
        this.passwordRepository.save(password);

        return password;

    }


//update 

  public Password update(Password password) {
        Optional<Password> Password =  passwordRepository.findById(password.getId());
        if (Password.isPresent()) {
            Password updatedPassword = Password.get();
            updatedPassword.setPasswordValue(password.getPasswordValue());
            updatedPassword.setDateModif(LocalDateTime.now());
            
            return  passwordRepository.save(updatedPassword);
        } else {
            throw new RuntimeException("Note not found with id " + password.getId());
        }
    }

//demande de reset password:

 // Méthode pour demander la réinitialisation du mot de passe
 public void requestPasswordReset(String email) throws Exception {

    Optional<UtilisateurResponse> optionalUserResponse = userFeignClient.getUserByEmail(email);
    if (!optionalUserResponse.isPresent()) {
        throw new Exception("User not found with email: " + email);
    }

    UtilisateurResponse userResponse = optionalUserResponse.get();
    
    // Génère un token de réinitialisation unique
    String token = UUID.randomUUID().toString();
    PasswordResetToken resetToken = new PasswordResetToken(token, userResponse.getEmail());

    // Sauvegarde le token dans le repository
    passwordResetTokenRepository.save(resetToken);

    // Crée un lien de réinitialisation de mot de passe
    String resetLink = "https://example.com/reset-password?token=" + token;

   //envoie emmail
    System.out.println("Email sent to " + userResponse.getEmail()+ " with reset link: " + resetLink);
}

//Action rest password


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


    

    // Récupère l'objet Password de l'Optional après avoir vérifié qu'il est présent
    Password password = optionalPassword.get();
    // Met à jour le champ du mot de passe de l'objet Password avec le nouveau mot de passe 
    password.setId(hashPassword(newPassword));
    // Met à jour la date de modification avec l'horodatage actuel pour indiquer que le mot de passe a été modifié
    password.setDateModif(LocalDateTime.now());
    // Sauvegarde l'objet Password mis à jour dans le repository, ce qui persiste les modifications dans la base de données
    passwordRepository.save(password);
}

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
   


}



