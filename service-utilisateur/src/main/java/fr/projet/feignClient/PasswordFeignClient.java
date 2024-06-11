package fr.projet.feignClient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "service-gestion-password", path = "/api/password", fallback = PasswordFeignClient.Fallback.class)
public interface PasswordFeignClient {
    

@GetMapping("/utilisateur/{id}")
public String getPasswordByUserId(@PathVariable("id") String id);

@PostMapping("/utilisateur/request-reset")
    void requestPasswordReset(@RequestParam String email);

    @PostMapping("/utilisateur/reset")
    void resetPassword(@RequestParam String token, @RequestParam String newPassword);

  
@PutMapping("/utilisateur/update")
    void updatePassword(@RequestParam("id") String id, @RequestParam("newPassword") String newPassword);


 @Component
    public static class Fallback implements PasswordFeignClient{
       

        @Override
        public String getPasswordByUserId(String id) {
           return "not iD found";
        }


        @Override
        public void resetPassword(String token, String newPassword) {
        
        }

        @Override
        public void requestPasswordReset(String email) {
 
    }


        @Override
        public void updatePassword(String id, String newPassword) {
            // TODO A stub
        }
    }

}
