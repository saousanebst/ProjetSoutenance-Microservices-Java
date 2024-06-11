package fr.projet.OpenFeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fr.projet.model.Compte;

@FeignClient(value = "service-utilisateur", path = "/api/utilisateur", fallback = UtilisateurFeignClient.Fallback.class)
public interface UtilisateurFeignClient {
    


@GetMapping("/api/utilisateur/{id}")
public Compte getCompteByUserId(@PathVariable("id") String id);

public class Fallback {
}
}
