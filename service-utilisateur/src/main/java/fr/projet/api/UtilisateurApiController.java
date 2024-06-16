package fr.projet.api;

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
import org.springframework.web.server.ResponseStatusException;

import fr.projet.api.dto.ConnexionDTO;
import fr.projet.api.dto.InscriptionDTO;
import fr.projet.feignClient.CompteFeignClient;
import fr.projet.feignClient.NoteFeignClient;
import fr.projet.feignClient.PasswordFeignClient;
import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;
import fr.projet.response.CompteResponse;
import fr.projet.response.NoteResponse;

import fr.projet.response.UtilisateurResponse;



@RestController
@RequestMapping("api/utilisateur")
public class UtilisateurApiController {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurApiController.class);



@Autowired 
 private UtilisateurRepository utilisateurRepository;
 @Autowired
 private UtilisateurResponse utilisateurResponse;

@Autowired
private CompteFeignClient compteFeignClient;
@Autowired
private NoteFeignClient noteFeignClient;
@Autowired
private PasswordFeignClient passwordFeignClient;



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
return ResponseEntity.notFound().build();

}
}



@PostMapping("/connexion")
	public Utilisateur connexion(@RequestBody ConnexionDTO connexionDTO) {
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmailAndPasswordValue(connexionDTO.getEmail(), connexionDTO.getPasswordValue());
		
		if(optUtilisateur.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		return optUtilisateur.get();
	}
	

    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody InscriptionDTO inscriptionDTO) {
        // Vérifie si un utilisateur avec cet e-mail existe déjà
        if (utilisateurRepository.existsByEmail(inscriptionDTO.getEmail())) {
            // Renvoie un message d'erreur si l'e-mail est déjà utilisé
            return ResponseEntity.status(HttpStatus.CONFLICT).body("L'e-mail existe déjà. Veuillez en choisir un autre.");
        }
    
        Utilisateur utilisateur = new Utilisateur();
        // Copie les propriétés de même type et nom depuis inscriptionDTO vers utilisateur
        BeanUtils.copyProperties(inscriptionDTO, utilisateur);
        utilisateur = this.utilisateurRepository.save(utilisateur);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateur);
    }


//reset

 @PostMapping("/request-reset")
 public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
     passwordFeignClient.requestPasswordReset(email);
     return ResponseEntity.ok("Password reset email has been sent.");
 }

 @PostMapping("/reset")
 public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
     passwordFeignClient.resetPassword(token, newPassword);
     return ResponseEntity.ok("Password has been reset.");
 }
// @PutMapping("/update")
//     public ResponseEntity<Void> updatePassword(@RequestParam("id") String idUser, @RequestParam("newPassword") String newPassword) {
       
//         passwordFeignClient.updatePassword(idUser, newPassword);

//         return ResponseEntity.noContent().build();
//     }

@PutMapping("/update")
public void updatePassword(String idUser, String newPassword) {
    Optional<Utilisateur> optionalUser = utilisateurRepository.findById(idUser);
    if (optionalUser.isPresent()) {
        Utilisateur user = optionalUser.get();
        user.setPassword(newPassword);
        utilisateurRepository.save(user);

        // Met à jour le mot de passe dans le service Password
        //passwordFeignClient.resetPassword(userId, newPassword);
    } else {
        throw new RuntimeException("User not found");
    }
}








}
