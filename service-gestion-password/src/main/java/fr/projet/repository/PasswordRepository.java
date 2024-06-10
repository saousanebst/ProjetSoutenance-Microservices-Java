package fr.projet.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.Password;

public interface PasswordRepository extends JpaRepository<Password, String> {
    
   public  Password findByidUser(String idUser);

   
}



