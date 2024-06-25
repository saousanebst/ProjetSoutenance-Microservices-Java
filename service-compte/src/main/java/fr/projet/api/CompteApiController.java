package fr.projet.api;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.OpenFeignClient.PasswordFeignClient;
import fr.projet.model.Compte;
import fr.projet.model.PrivateKey;
import fr.projet.repository.CompteRepository;
import fr.projet.repository.PrivateKeyRepository;
import fr.projet.request.CreateCompteRequest;
import fr.projet.request.PasswordCheckRequest;
import fr.projet.response.CompteResponse;
import fr.projet.response.PasswordCheckResponse;
import fr.projet.response.PasswordGeneratedResponse;
import fr.projet.service.CompteSrv;
import fr.projet.service.CryptographService;

@RestController
@RequestMapping("/api/compte")
// @CrossOrigin("*")

public class CompteApiController {
    


@Autowired
private CompteRepository compteRepository;
@Autowired
private CompteSrv compteService;

@Autowired
private PasswordFeignClient passwordFeignClient;

@Autowired
private CryptographService cryptographService;


@Autowired
private PrivateKeyRepository  privateKeyRepository;
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
    try {
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le mot de passe n'est pas assez fort. Mot de passe suggéré : " + generatedResponse.getPassword());
        }

        // Générer une paire de clés RSA
        KeyPair keyPair = cryptographService.generateKeyPair();

        // Chiffrer le mot de passe avec la clé publique
        String publicKeyStr = cryptographService.encodePublicKey(keyPair.getPublic());
        String encryptedPassword = cryptographService.encryptPasswordWithPublicKey(request.getPassword(), publicKeyStr);

        // Enregistrer le compte avec le mot de passe chiffré et la clé publique
        Compte compte = new Compte();
        BeanUtils.copyProperties(request, compte);
        compte.setPassword(encryptedPassword);
        compte.setPublicKey(publicKeyStr);
        compte.setIdUser(request.getUserId());  // Associer le compte à l'utilisateur
        this.compteRepository.save(compte);

        // Enregistrer la clé privée associée au compte
        PrivateKey privateKey = new PrivateKey();
        privateKey.setCompteId(compte.getId());
        privateKey.setPrivateKey(cryptographService.encodePrivateKey(keyPair.getPrivate()));
        this.privateKeyRepository.save(privateKey);

        return ResponseEntity.status(HttpStatus.CREATED).body(compte.getId());
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.OK).body("Clé privée non trouvée pour ce compte");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création du compte : " + e.getMessage());
    }
}



        @PostMapping("/decryptPassword")
public ResponseEntity<String> decryptPassword(@RequestParam String compteId) {
    try {
        // Récupérer les informations du compte et la clé privée
        Optional<Compte> compteOptional = compteRepository.findById(compteId);
        if (!compteOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Compte non trouvé");
        }

        Compte compte = compteOptional.get();
        Optional<PrivateKey> privateKeyOptional = privateKeyRepository.findByCompteId(compteId);
        if (!privateKeyOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Clé privée non trouvée pour ce compte");
        }

        // Décrypter le mot de passe avec la clé privée
        String encryptedPassword = compte.getPassword();
        String privateKeyStr = privateKeyOptional.get().getPrivateKey();
        String decryptedPassword = cryptographService.decryptPassword(encryptedPassword, privateKeyStr);

        return ResponseEntity.ok(decryptedPassword);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du déchiffrement du mot de passe : " + e.getMessage());
    }
}





//update

@PutMapping("/update/{id}")
	public Compte updateCompte (@PathVariable String id,@RequestBody Compte compte) 
	{
		compte.setId(id);
		return compteService.update(compte);
	}
	
//delete	
	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable String id) 
	{
		compteService.deleteCompteById(id);
	}

//check strength and vulnerability
 
    @PostMapping("/check-strength")
    public ResponseEntity<PasswordCheckResponse> checkPasswordStrength(@RequestBody PasswordCheckRequest request) {
        PasswordCheckResponse response = passwordFeignClient.checkPasswordStrength(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-vulnerability")
    public ResponseEntity<PasswordCheckResponse> checkPasswordVulnerability(@RequestBody PasswordCheckRequest request) {
        PasswordCheckResponse response = passwordFeignClient.checkPasswordVulnerability(request);
        return ResponseEntity.ok(response);
    }

    //generate

    @PostMapping("/generate")
    public ResponseEntity<PasswordGeneratedResponse> generatePassword() {
        PasswordGeneratedResponse response = passwordFeignClient.generatePassword();
        return ResponseEntity.ok(response);
    }



}













