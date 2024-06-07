package fr.projet.OpenFeignClient;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.projet.Response.PasswordResponseId;
import fr.projet.Response.UtilisateurResponse;



@FeignClient(value= "service-utilisateur", path = "api/utilisateur")
public interface UserFeignClient {
    @GetMapping("/email")
    Optional<UtilisateurResponse> getUserByEmail(@RequestParam String email);

    @GetMapping("/idPassword")
    UtilisateurResponse getUserByPasswordId(@PathVariable String id);



    @PutMapping("/{userId}/password")
    void updatePassword(@PathVariable("userId") String userId, @RequestParam String newPassword);
}



