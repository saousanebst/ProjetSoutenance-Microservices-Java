package fr.projet.api;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.projet.api.dto.ConnexionDTO;
import fr.projet.api.dto.InscriptionDTO;
import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;
import fr.projet.api.dto.ConnexionDTO;

@RestController
@RequestMapping("api/utilisateur")
public class UtilisateurApiController {
@Autowired 
 private UtilisateurRepository utilisateurRepository;

    



@PostMapping("/connexion")
	public Utilisateur connexion(@RequestBody ConnexionDTO connexionDTO) {
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmailAndPassword(connexionDTO.getEmail(), connexionDTO.getPassword());
		
		if(optUtilisateur.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		return optUtilisateur.get();
	}
	

@PostMapping("/inscription")
	public Utilisateur inscription(@RequestBody InscriptionDTO inscriptionDTO) {
		
		Utilisateur utilisateur = new Utilisateur();
      // copie les propriétés de même type et nom depuis inscriptionDTO vers utilisateur
		BeanUtils.copyProperties(inscriptionDTO, utilisateur);
        utilisateur = this.utilisateurRepository.save(utilisateur);

		return utilisateur;
	}


}
