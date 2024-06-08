package fr.projet.feignClient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "service-gestion-password", path = "/api/password", fallback = PaaswordFeignClient.Fallback.class)
public interface PaaswordFeignClient {
    

@GetMapping("/utilisateur/{id}")
public String getPasswordByUserId(@PathVariable("id") String id);

 @Component
    public static class Fallback implements PaaswordFeignClient{
       

        @Override
        public String getPasswordByUserId(String id) {
           return "not iD found";
        }
    }

}
