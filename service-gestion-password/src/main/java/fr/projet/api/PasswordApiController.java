package fr.projet.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.model.Password;
import fr.projet.service.PasswordService;

@RestController
@RequestMapping("/api/password")
@CrossOrigin("*")
public class PasswordApiController {
    

@Autowired
private PasswordService passwordSrv;


@PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        passwordSrv.resetPassword(email);
        return ResponseEntity.ok("Un email de réinitialisation a été envoyé à l'adresse spécifiée.");
    }






























}
