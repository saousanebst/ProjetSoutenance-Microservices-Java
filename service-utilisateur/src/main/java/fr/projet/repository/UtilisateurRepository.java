package fr.projet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,String > {

     Optional<Utilisateur> findByEmailAndPasswordValue(String email, String passwordValue);

      Optional<Utilisateur> findByEmail(String email);
      boolean existsByEmail(String email);

} 
