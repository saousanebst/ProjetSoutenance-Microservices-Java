package fr.projet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.Compte;

public interface CompteRepository extends JpaRepository<Compte,String> {
 Optional<Compte> findById(String id);
public List<Compte> findAllByIdUser (String idUser);






}
