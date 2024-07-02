package fr.projet.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
import fr.projet.api.dto.ConnexionDTO;
import fr.projet.api.dto.InscriptionDTO;
import fr.projet.api.dto.UtilisateurDto;
import fr.projet.feignClient.CompteFeignClient;
import fr.projet.feignClient.NoteFeignClient;
import fr.projet.feignClient.PasswordFeignClient;
import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;
import fr.projet.request.PasswordCheckRequest;
import fr.projet.response.CompteResponse;
import fr.projet.response.NoteResponse;
import fr.projet.response.PasswordCheckResponse;
import fr.projet.response.PasswordGeneratedResponse;
import fr.projet.response.UtilisateurResponse;
import fr.projet.service.UtilisateurLogService;
import fr.projet.service.UtilisateurService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


// @CrossOrigin("*")
@RestController
@RequestMapping("api/utilisateur")
public class UtilisateurApiController {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurApiController.class);



@Autowired 
 private UtilisateurRepository utilisateurRepository;
 @Autowired
 private UtilisateurResponse utilisateurResponse;
@Autowired
UtilisateurService utilisateurService;
@Autowired
private CompteFeignClient compteFeignClient;
@Autowired
private NoteFeignClient noteFeignClient;
@Autowired
private PasswordFeignClient passwordFeignClient;

    @Autowired
    private UtilisateurLogService logService;

@GetMapping("/by-email")
    public ResponseEntity<UtilisateurDto> getUserByEmail(@RequestParam("email") String email) {
        Optional<Utilisateur> utilisateur = utilisateurService.findByEmail(email);
        
        if (utilisateur.isPresent()) {
            UtilisateurDto utilisateurDTO = new UtilisateurDto();
            utilisateurDTO.setEmail(utilisateur.get().getEmail());
            utilisateurDTO.setId(utilisateur.get().getId());
            logService.logInfo("Fetched user by email: " + email);

            return ResponseEntity.ok(utilisateurDTO);
        } else {
            logService.logWarn("User not found with email: " + email);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable String id, @RequestBody String hashedPassword) {
        try {
            utilisateurService.updatePassword(id, hashedPassword);
            logService.logInfo("Updated password for user with ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logService.logError("Error updating password for user with ID: " + id + ". Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    


@GetMapping()
public List<UtilisateurResponse> findAll() {
    List<Utilisateur> utilisateurs = this.utilisateurRepository.findAll();
    List<UtilisateurResponse> response = new ArrayList<>();

    for (Utilisateur utilisateur : utilisateurs) {
        UtilisateurResponse utilisateurResponse = new UtilisateurResponse();

        BeanUtils.copyProperties(utilisateur, utilisateurResponse);

         List<CompteResponse> comptes = compteFeignClient.getComptesByUtilisateurId(utilisateur.getId());
        List<NoteResponse> notes = noteFeignClient.getNotesByUtilisateurId(utilisateur.getId());

        String password = passwordFeignClient.getPasswordByUserId(utilisateur.getId());
        utilisateurResponse.setComptes(comptes);
        utilisateurResponse.setNotes(notes);
        utilisateurResponse.setPasswordValue(password);

           response.add(utilisateurResponse);
         }
        
        return response;
}

@GetMapping("/{id}")
public ResponseEntity<UtilisateurResponse> findById(@PathVariable("id") String id) {
    logService.logInfo("Fetching user details for ID: " + id);

    Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findById(id);

    if (utilisateurOptional.isPresent()) {
        Utilisateur utilisateur = utilisateurOptional.get();
        UtilisateurResponse utilisateurResponse = new UtilisateurResponse();
       BeanUtils.copyProperties(utilisateur, utilisateurResponse);

        List<CompteResponse> comptes = compteFeignClient.getComptesByUtilisateurId(utilisateur.getId());
        List<NoteResponse> notes = noteFeignClient.getNotesByUtilisateurId(utilisateur.getId());

        logger.info("Fetching password for user ID: " + utilisateur.getId());

        String password = passwordFeignClient.getPasswordByUserId(utilisateur.getId());

        utilisateurResponse.setComptes(comptes);
        utilisateurResponse.setNotes(notes);
        logger.info("Retrieved password: " + password);

        utilisateurResponse.setPasswordValue(password);

        logger.info("User details before saving: " + utilisateur.toString());

        utilisateur.setPassword(password);
        utilisateurRepository.save(utilisateur);
        logger.info("User details after saving: " + utilisateur.toString());

    

return ResponseEntity.ok(utilisateurResponse);
} else {
    logService.logWarn("User not found with ID: " + id);

return ResponseEntity.notFound().build();

}
}


@PostMapping("/connexion")
    public ResponseEntity<Object> connexion(@RequestBody ConnexionDTO connexionDTO) {
        logService.logInfo("Tentative de connexion pour l'email: " + connexionDTO.getEmail());

        Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmail(connexionDTO.getEmail());

        if (optUtilisateur.isEmpty()) {
            logService.logWarn("L'email n'existe pas: " + connexionDTO.getEmail());
            return new ResponseEntity<>("L'email n'existe pas", HttpStatus.NOT_FOUND);
        }

        Utilisateur utilisateur = optUtilisateur.get();

        if (!utilisateur.getPassword().equals(connexionDTO.getPasswordValue())) {
            logService.logWarn("Mot de passe incorrect pour l'utilisateur avec l'email: " + connexionDTO.getEmail());
            return new ResponseEntity<>("Le mot de passe est incorrect", HttpStatus.UNAUTHORIZED);
        }

        logService.logInfo("Connexion réussie pour l'email: " + connexionDTO.getEmail());
        return new ResponseEntity<>(utilisateur, HttpStatus.OK);
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody InscriptionDTO inscriptionDTO) {
        try {
        // Vérifie si un utilisateur avec cet e-mail existe déjà
        logService.logInfo("Attempting to register new user");

        if (utilisateurRepository.existsByEmail(inscriptionDTO.getEmail())) {
            logService.logWarn("Email already exists: " + inscriptionDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("L'e-mail existe déjà. Veuillez en choisir un autre.");
        }

        // Vérifie si la date de naissance est supérieure à la date du jour
        LocalDate today = LocalDate.now();
        if (inscriptionDTO.getBirthdate().isAfter(today)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La date de naissance ne peut pas être dans le futur.");
        }

        // Vérifie si la date de naissance est égale à la date du jour
        if (inscriptionDTO.getBirthdate().isEqual(today)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La date de naissance ne peut pas être aujourd'hui.");
        }

        // Vérification de la vulnérabilité du mot de passe
        PasswordCheckRequest passwordCheckRequest = new PasswordCheckRequest(inscriptionDTO.getPassword());
        PasswordCheckResponse vulnerabilityResponse = passwordFeignClient.checkPasswordVulnerability(passwordCheckRequest);
        if (vulnerabilityResponse.isVulnerable()) {
            logService.logWarn("Vulnerable password for user: " + inscriptionDTO.getEmail());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le mot de passe est vulnérable");
        }

        // Vérification de la force du mot de passe
        PasswordCheckResponse strengthResponse = passwordFeignClient.checkPasswordStrength(passwordCheckRequest);
        if (!strengthResponse.isStrong()) {
            logService.logWarn("Password not strong enough for user: " + inscriptionDTO.getEmail());

            PasswordGeneratedResponse generatedResponse = passwordFeignClient.generatePassword();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le mot de passe n'est pas suffisamment fort. Mot de passe suggéré : " + generatedResponse.getPassword());
        }

        // Création de l'utilisateur dans le service utilisateur via Feign Client
        Utilisateur utilisateur = new Utilisateur();
        BeanUtils.copyProperties(inscriptionDTO, utilisateur);
        utilisateur = this.utilisateurRepository.save(utilisateur);
        logService.logInfo("User registered successfully: " + utilisateur.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateur);
    } catch (FeignException e) {
        logService.logError("Error during registration: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'inscription : " + e.getMessage());
    } catch (Exception e) {
        logService.logError("Error during registration: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'inscription : " + e.getMessage());
    }
    }


//reset
@PostMapping("/request-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        logService.logInfo("Requesting password reset for email: " + email);
        passwordFeignClient.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset email has been sent.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        logService.logInfo("Resetting password with token: " + token);
        passwordFeignClient.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset.");
    }

@PutMapping("/update")
public void updatePassword(String idUser, String newPassword) {
    logService.logInfo("Updating password for user with ID: " + idUser);

    try {
        Optional<Utilisateur> optionalUser = utilisateurRepository.findById(idUser);
        if (optionalUser.isPresent()) {
            Utilisateur user = optionalUser.get();
            user.setPassword(newPassword);
            utilisateurRepository.save(user);

           
            logService.logInfo("Password updated successfully for user with ID: " + idUser);
        } else {
            throw new RuntimeException("User not found");
        }
    } catch (Exception e) {
        logService.logError("Error updating password for user with ID: " + idUser + ". Error: " + e.getMessage());
        throw e;
    }
}





@PostMapping("/deconnexion")
public ResponseEntity<String> deconnexion(HttpServletRequest request) {
    logService.logInfo("Logging out user");

    HttpSession session = request.getSession(false); // Récupère la session sans en créer une nouvelle
    if (session != null) {
        session.invalidate(); // Invalide la session actuelle
        logService.logInfo("User logged out successfully.");
        return ResponseEntity.ok("Utilisateur déconnecté avec succès.");
    } else {
        logService.logWarn("Session not found during logout.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session non trouvée.");
    }
}




}
