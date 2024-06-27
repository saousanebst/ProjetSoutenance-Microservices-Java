package fr.projet.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.model.UserNotFoundException;
import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;

@Service
public class UtilisateurService {
     @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }


   

    public void updatePassword(String idUser, String hashedPassword) {
        Optional<Utilisateur> optionalUser = utilisateurRepository.findById(idUser);
        
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("User not found");
        }
        
        Utilisateur user = optionalUser.get();
        user.setPassword(hashedPassword);
        utilisateurRepository.save(user);
    }


}
