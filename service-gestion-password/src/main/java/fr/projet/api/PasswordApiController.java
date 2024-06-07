package fr.projet.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.OpenFeignClient.UserFeignClient;
import fr.projet.Request.CreatePasswordRequest;
import fr.projet.Response.PasswordResponse;
import fr.projet.Response.PasswordResponseId;
import fr.projet.Response.UtilisateurResponse;
import fr.projet.model.Password;
import fr.projet.repository.PasswordRepository;
import fr.projet.service.PasswordService;

@RestController
@RequestMapping("/api/password")
@CrossOrigin("*")
public class PasswordApiController {
    

@Autowired
private PasswordService passwordSrv;

@Autowired
private PasswordRepository passwordRepository;

@Autowired
private UserFeignClient userFeignClient;



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

@PutMapping("/{id}")
	public Password updatePassword (@PathVariable String id,@RequestBody Password password) 
	{
		password.setId(id);
		return passwordSrv.update(password);
	}

    // @PutMapping("/{userId}/password")
    // public ResponseEntity<PasswordResponse> updatePassword(@PathVariable String id, @RequestBody Password password) {
    //     password.setId(id);
    //     Password updatedPassword = passwordSrv.update(password);
        
    //     PasswordResponse response = new PasswordResponse();
    //     BeanUtils.copyProperties(updatedPassword, response);
        
    //     return ResponseEntity.ok(response);}
    
	
   

    // Endpoint pour demander une réinitialisation de mot de passe
    @PostMapping("/request-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) throws Exception {
        // Appelle le service pour traiter la demande de réinitialisation de mot de passe
        passwordSrv.requestPasswordReset(email);
        // Retourne une réponse indiquant que l'e-mail de réinitialisation a été envoyé
        return ResponseEntity.ok("Password reset email has been sent.");
    }


    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        passwordSrv.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset.");
    }


    @GetMapping("/{id}")
    public ResponseEntity<PasswordResponse> getPasswordById(@PathVariable String id) {
        Optional<Password> optionalPassword = passwordRepository.findById(id);
        if (optionalPassword.isPresent()) {
            Password password = optionalPassword.get();
            PasswordResponse response = new PasswordResponse();
            BeanUtils.copyProperties(password, response);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    // @GetMapping("/{id}")
    // public ResponseEntity<PasswordResponse> findById(@PathVariable("id") String id) {
    //     Optional<Password> passwordOptional = this.passwordRepository.findById(id);
    
    //     if (passwordOptional.isPresent()) {
    //         Password password = passwordOptional.get();
    //         PasswordResponse passwordResponse = new PasswordResponse();
    //         BeanUtils.copyProperties(password, passwordResponse);
    
    //         // Assurez-vous que la méthode getUserByPasswordId retourne un UtilisateurResponse
    //         UtilisateurResponse utilisateurResponse = userFeignClient.getUserByIdPassword(password);
    
    //         // Copier les propriétés utilisateur dans le PasswordResponse
    //         if (utilisateurResponse != null) {
    //             passwordResponse.setUserId(utilisateurResponse.getId());
                
    //             // Ajouter d'autres propriétés utilisateur si nécessaire
    //         }
    
    //         return ResponseEntity.ok(passwordResponse);
    //     } else {
    //         return ResponseEntity.notFound().build();
    //     }
    // }



    

}
