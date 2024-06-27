package fr.projet.OpenFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import fr.projet.DTO.UtilisateurDto;

@FeignClient(name = "service-utilisateur", url = "http://localhost:8082/api/utilisateur") 
public interface UserServiceClient {
     @GetMapping("/by-email")
    UtilisateurDto getUserByEmail(@RequestParam("email") String email);
     @PutMapping("/{id}/password")
    void updateUserPassword(@PathVariable("id") String idUser, @RequestBody String hashedPassword);
}
