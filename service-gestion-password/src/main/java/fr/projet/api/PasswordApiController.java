package fr.projet.api;

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

import fr.projet.Request.CreatePasswordRequest;
import fr.projet.Response.PasswordResponse;
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



}
