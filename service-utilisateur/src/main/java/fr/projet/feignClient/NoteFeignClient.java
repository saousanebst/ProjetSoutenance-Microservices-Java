package fr.projet.feignClient;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fr.projet.response.NoteResponse;

@FeignClient(value = "service-notes", path = "/api/note", fallback = NoteFeignClient.Fallback.class)
public interface NoteFeignClient {
    
    @GetMapping("/utilisateur/{id}")
    public List<NoteResponse> getNotesByUtilisateurId (@PathVariable("id") String id);

    @Component
    public static class Fallback implements NoteFeignClient{
        @Override
        public  List<NoteResponse> getNotesByUtilisateurId(String id){
         return Collections.emptyList();
          
        }
    }
}



