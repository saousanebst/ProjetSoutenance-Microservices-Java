package fr.projet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.Password;

public interface PasswordRepository extends JpaRepository<Password, String> {
    

public List <Password> findAllByIdUser(String idUser);


}