package fr.projet.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "service-compte", url = "http://localhost:8081", path="/api/compte") 
public interface CompteFeignClient {
    @GetMapping("/api/comptes/utilisateur/{id}")
    public List<Compte> getComptesByUtilisateurId(@PathVariable("id") String id);
}






