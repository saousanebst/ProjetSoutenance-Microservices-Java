// package fr.projet.service;

// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import fr.projet.model.Utilisateur;
// import fr.projet.repository.UtilisateurRepository;

// @Service
// public class UtilisateurService {

// @Autowired
// UtilisateurRepository utilisateurRepository;
    
// //méthode inscription
// public Utilisateur inscrire( Utilisateur utilisateur){
//     if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
//         throw new RuntimeException("Email existe déja ");
//     }
//     return utilisateurRepository.save(utilisateur);

// }
// //se connecter 


// public Utilisateur login(String email, String password){

//     Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

//     if(utilisateurOpt.isPresent()){
//         Utilisateur utilisateur = utilisateurOpt.get();
        
//     }


//     return utilisateur;
// }





