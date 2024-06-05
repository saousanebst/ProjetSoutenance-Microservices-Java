package fr.projet.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "service-compte", path="/api/compte") 
public interface CompteFeignClient {
    @GetMapping("/{id}")
    public String getComptesByUtilisateurId(@PathVariable("id") String id);
}






