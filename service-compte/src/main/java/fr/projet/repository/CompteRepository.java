package fr.projet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.Compte;

public interface CompteRepository extends JpaRepository<Compte,String> {
     Optional<Compte> findById(String id);
}
