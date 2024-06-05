package fr.projet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.Compte;

public interface CompteRepository extends JpaRepository<Compte,String> {
     List<Compte> findAllById(Iterable<String> ids);
}
