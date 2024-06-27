package fr.projet.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;

@Service
public class UtilisateurService {
     @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
}
