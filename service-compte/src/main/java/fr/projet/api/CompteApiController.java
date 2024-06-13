package fr.projet.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.OpenFeignClient.PasswordFeignClient;
import fr.projet.model.Compte;
import fr.projet.repository.CompteRepository;
import fr.projet.request.CreateCompteRequest;
import fr.projet.request.PasswordCheckRequest;
import fr.projet.response.CompteResponse;
import fr.projet.response.PasswordCheckResponse;
import fr.projet.response.PasswordGeneratedResponse;
import fr.projet.service.CompteSrv;

@RestController
@RequestMapping("/api/compte")
@CrossOrigin("*")

public class CompteApiController {
    
@Autowired
private CompteRepository compteRepository;
@Autowired
private CompteSrv compteService;

@Autowired
private PasswordFeignClient passwordFeignClient;


//findAll
@GetMapping
    public List<CompteResponse> findAll() {
        List<Compte> comptes = this.compteRepository.findAll();
        List<CompteResponse> response = new ArrayList<>();

        for (Compte compte : comptes) {
            CompteResponse compteResponse = new CompteResponse();

            BeanUtils.copyProperties(compte, compteResponse);

            response.add(compteResponse);

            }
        
        
        return response;
    }


 // findAll by userId
    @GetMapping("/utilisateur/{idUser}")
    public List<CompteResponse> getComptesByUtilisateurId(@PathVariable String idUser) {
        List<Compte> comptes = compteRepository.findAllByIdUser(idUser);
        List<CompteResponse> response = new ArrayList<>();

        for (Compte compte : comptes) {
            CompteResponse compteResponse = new CompteResponse();
            BeanUtils.copyProperties(compte, compteResponse);
            response.add(compteResponse);
        }

        return response;
    }

     @GetMapping("/{id}")
    public CompteResponse findById(@PathVariable String id) {
        Compte compte = compteService.findById(id);
        CompteResponse compteResponse = new CompteResponse();
        BeanUtils.copyProperties(compte, compteResponse);
        return compteResponse;
    }


    @PostMapping("/ajout")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@RequestBody CreateCompteRequest request) {
        // Vérification de la vulnérabilité du mot de passe
        PasswordCheckRequest passwordCheckRequest = new PasswordCheckRequest(request.getPassword());
        PasswordCheckResponse passwordCheckResponse = passwordFeignClient.checkPasswordVulnerability(passwordCheckRequest);
    
        // Si le mot de passe est vulnérable, renvoyer une réponse d'erreur
        if (passwordCheckResponse.isVulnerable()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le mot de passe est vulnérable");
        }

        // Vérification de la force du mot de passe
        PasswordCheckResponse strengthResponse = passwordFeignClient.checkPasswordStrength(passwordCheckRequest);
        if (!strengthResponse.isStrong()) {
            // Générer un mot de passe fort
            PasswordGeneratedResponse generatedResponse = passwordFeignClient.generatePassword();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is not strong enough. Suggested password: " + generatedResponse.getPassword());
        }

        try {
            // Chiffrer le mot de passe avec RSA
            String encryptedPassword = passwordFeignClient.encryptPassword(request.getPassword());

            // Si le mot de passe est valide et fort, créer le compte
            Compte compte = new Compte();
            BeanUtils.copyProperties(request, compte);
            compte.setPassword(encryptedPassword); // Enregistrer le mot de passe chiffré
            this.compteRepository.save(compte);

            return ResponseEntity.status(HttpStatus.CREATED).body(compte.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du chiffrement du mot de passe");
        }
    }


// //Create

//  @PostMapping("/ajout")
//     @ResponseStatus(HttpStatus.CREATED)
//     public ResponseEntity<String> create(@RequestBody CreateCompteRequest request) {
//         // Appeler le service de vérification de la vulnérabilité du mot de passe
//         PasswordCheckRequest passwordCheckRequest = new PasswordCheckRequest(request.getPassword());
//         PasswordCheckResponse passwordCheckResponse = passwordFeignClient.checkPasswordVulnerability(passwordCheckRequest);
    
//         // Si le mot de passe est vulnérable, renvoyer une réponse d'erreur
//         if (passwordCheckResponse.isVulnerable()) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le mot de passe est vulnérable");
//         }


//   // Check password strength
//   PasswordCheckResponse strengthResponse = passwordFeignClient.checkPasswordStrength(passwordCheckRequest);
//   if (!strengthResponse.isStrong()) {
//       // Generate strong password
//       PasswordGeneratedResponse generatedResponse = passwordFeignClient.generatePassword();
//       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is not strong enough. Suggested password: " + generatedResponse.getPassword());
//   }

//          // Si le mot de passe est valide et fort, créer le compte
//         Compte compte = new Compte();


//         BeanUtils.copyProperties(request, compte);
//         this.compteRepository.save(compte);
    
//         return ResponseEntity.status(HttpStatus.CREATED).body(compte.getId());
//     }



// //update

// @PutMapping("/{id}")
// 	public Compte updateCompte (@PathVariable String id,@RequestBody Compte compte) 
// 	{
// 		compte.setId(id);
// 		return compteService.update(compte);
// 	}
	
// //delete	
// 	@DeleteMapping("/{id}")
// 	public void deleteById(@PathVariable String id) 
// 	{
// 		compteService.deleteCompteById(id);
// 	}

// //check strength and vulnerability
 
//     @PostMapping("/check-strength")
//     public ResponseEntity<PasswordCheckResponse> checkPasswordStrength(@RequestBody PasswordCheckRequest request) {
//         PasswordCheckResponse response = passwordFeignClient.checkPasswordStrength(request);
//         return ResponseEntity.ok(response);
//     }

//     @PostMapping("/check-vulnerability")
//     public ResponseEntity<PasswordCheckResponse> checkPasswordVulnerability(@RequestBody PasswordCheckRequest request) {
//         PasswordCheckResponse response = passwordFeignClient.checkPasswordVulnerability(request);
//         return ResponseEntity.ok(response);
//     }

//     //generate

//     @PostMapping("/generate")
//     public ResponseEntity<PasswordGeneratedResponse> generatePassword() {
//         PasswordGeneratedResponse response = passwordFeignClient.generatePassword();
//         return ResponseEntity.ok(response);
//     }

}















