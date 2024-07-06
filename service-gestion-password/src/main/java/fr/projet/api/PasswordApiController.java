package fr.projet.api;



import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.Request.CreatePasswordRequest;
import fr.projet.Request.PasswordCheckRequest;
import fr.projet.Response.PasswordCheckResponse;
import fr.projet.Response.PasswordGeneratedResponse;
import fr.projet.model.Password;
import fr.projet.repository.PasswordRepository;
import fr.projet.service.PasswordService;

@RestController
@RequestMapping("/api/password")
// @CrossOrigin("*")
public class PasswordApiController {
    

@Autowired
private PasswordService passwordSrv;

@Autowired
private PasswordRepository passwordRepository;



// //create 


@PostMapping("/ajout")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody CreatePasswordRequest request) {
        Password password = new Password();
        
        BeanUtils.copyProperties(request, password);

        this.passwordRepository.save(password);

        return password.getId();
    }


//update 

@PutMapping("/utilisateur/update")
public ResponseEntity<Void> updatePassword(@RequestParam("idUser") String idUser, @RequestParam("newPassword") String newPassword) {
    passwordSrv.updatePassword(idUser, newPassword);
    return ResponseEntity.noContent().build();
}


 // findAll by userId
    @GetMapping("/utilisateur/{idUser}")
    public String getPasswordByUserId(@PathVariable String idUser) {
        return passwordSrv.getPasswordByUserId(idUser);
    }


//reset



@PostMapping("/utilisateur/request-reset")
public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
    try {
        passwordSrv.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset email has been sent.");
    } catch (Exception e) {
        // Log the exception for debugging purposes
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Failed to send password reset email.");
    }
}



@PostMapping("/utilisateur/reset")
public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
    try {
        // Vérifier la force du nouveau mot de passe
        PasswordCheckRequest passwordRequest = new PasswordCheckRequest(newPassword);
        
        // Vérifier la force du mot de passe
        PasswordCheckResponse strengthResponse = passwordSrv.checkPasswordStrength(passwordRequest);

        // Vérifier la vulnérabilité du mot de passe
        PasswordCheckResponse vulnerabilityResponse = passwordSrv.checkPasswordVulnerability(passwordRequest);

        // Si le mot de passe est fort et non vulnérable, réinitialiser le mot de passe
        if (strengthResponse.isStrong() && !vulnerabilityResponse.isVulnerable()) {
            passwordSrv.resetPassword(token, newPassword); // Appel au service pour réinitialiser le mot de passe
            return ResponseEntity.ok("Password has been reset.");
        } else {
            // Construction du message d'erreur détaillé
            StringBuilder errorMessage = new StringBuilder("Le nouveau mot de passe ne répond pas aux critères requis:\n");
            if (!strengthResponse.isStrong()) {
                errorMessage.append("- Le mot de passe n'est pas assez fort.\n");
            }
            if (vulnerabilityResponse.isVulnerable()) {
                errorMessage.append("- Le mot de passe est vulnérable.\n");
            }

            // Retourner une réponse indiquant les raisons de l'échec
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
    } catch (Exception e) {
        // Log et renvoyer une erreur en cas d'échec
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Failed to reset password.");
    }
}

@GetMapping("/reset-password")
public String showResetPasswordForm(@RequestParam(name = "token") String token, Model model) {
    // Ici, vous pouvez ajouter la logique pour afficher le formulaire de réinitialisation
    model.addAttribute("token", token);
    return "reset-password"; // Assurez-vous que cette vue existe dans vos templates
}

   
@PostMapping("/compte/check-strength")
    public PasswordCheckResponse checkPasswordStrength(@RequestBody PasswordCheckRequest request) {
        // Implémentez la logique pour vérifier la force du mot de passe dans le service
        return passwordSrv.checkPasswordStrength(request);
    } 

    @PostMapping("/compte/check-vulnerability")
    public PasswordCheckResponse checkPasswordVulnerability(@RequestBody PasswordCheckRequest request) {
        // Implémentez la logique pour vérifier la vulnérabilité du mot de passe dans le service
        return passwordSrv.checkPasswordVulnerability(request);
    }

    @PostMapping("/compte/generate")
    public PasswordGeneratedResponse generatePassword() {
        // Implémentez la logique pour générer un mot de passe fort dans le service
        return passwordSrv.generatePassword();
    }

} 

