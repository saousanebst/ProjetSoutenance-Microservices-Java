package fr.projet.feignClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fr.projet.response.CompteResponse;


@FeignClient(value = "service-compte", path = "/api/compte", fallback = CompteFeignClient.Fallback.class)
public interface CompteFeignClient {
    @GetMapping("/utilisateur/{id}")
    public List<CompteResponse> getComptesByUtilisateurId(@PathVariable("id") String id);

    @Component
    public static class Fallback implements CompteFeignClient{
        @Override
        public  List<CompteResponse> getComptesByUtilisateurId(String id){
         return Collections.emptyList();
          
        }
    }

}


