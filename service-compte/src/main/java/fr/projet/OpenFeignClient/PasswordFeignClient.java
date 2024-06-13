package fr.projet.OpenFeignClient;


import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import fr.projet.request.PasswordCheckRequest;
import fr.projet.response.PasswordCheckResponse;
import fr.projet.response.PasswordGeneratedResponse;


@FeignClient(value = "service-gestion-password", path = "/api/password")
public interface PasswordFeignClient {

    @PostMapping("/compte/check-strength")
    PasswordCheckResponse checkPasswordStrength(@RequestBody PasswordCheckRequest request);


    @PostMapping("/compte/check-vulnerability")
    PasswordCheckResponse checkPasswordVulnerability(@RequestBody PasswordCheckRequest request);


    @PostMapping("/compte/generate")
    PasswordGeneratedResponse generatePassword();


    //@PostMapping("/api/password/encrypt")
    @PostMapping("/compte/encrypt")
    String encryptPassword(@RequestBody String plainPassword);

    
}


