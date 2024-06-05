package fr.projet.feignClient;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fr.projet.response.CompteResponse;


@FeignClient(value = "service-compte", path = "/api/compte") 
public interface CompteFeignClient {
    @GetMapping("/utilisateur/{idUser}")
    List<CompteResponse> getComptesByUtilisateurId(@PathVariable("idUser") String idUser);
}
