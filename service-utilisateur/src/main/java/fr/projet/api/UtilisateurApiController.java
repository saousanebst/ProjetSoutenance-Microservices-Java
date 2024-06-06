package fr.projet.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.projet.api.dto.ConnexionDTO;
import fr.projet.api.dto.InscriptionDTO;
import fr.projet.feignClient.CompteFeignClient;
import fr.projet.feignClient.NoteFeignClient;
import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;
import fr.projet.response.CompteResponse;
import fr.projet.response.NoteResponse;
import fr.projet.response.UtilisateurResponse;
import fr.projet.api.dto.ConnexionDTO;


@RestController
@RequestMapping("api/utilisateur")
public class UtilisateurApiController {

@Autowired 
 private UtilisateurRepository utilisateurRepository;
 @Autowired
 private UtilisateurResponse utilisateurResponse;

@Autowired
private CompteFeignClient compteFeignClient;
@Autowired
private NoteFeignClient noteFeignClient;

@GetMapping()
public List<UtilisateurResponse> findAll() {
    List<Utilisateur> utilisateurs = this.utilisateurRepository.findAll();
    List<UtilisateurResponse> response = new ArrayList<>();

    for (Utilisateur utilisateur : utilisateurs) {
        UtilisateurResponse utilisateurResponse = new UtilisateurResponse();

        BeanUtils.copyProperties(utilisateur, utilisateurResponse);

         List<CompteResponse> comptes = compteFeignClient.getComptesByUtilisateurId(utilisateur.getId());
        List<NoteResponse> notes = noteFeignClient.getNotesByUtilisateurId(utilisateur.getId());

        utilisateurResponse.setComptes(comptes);
        utilisateurResponse.setNotes(notes);
           

           response.add(utilisateurResponse);
         }
        
        return response;
}

@GetMapping("/{id}")
public ResponseEntity<UtilisateurResponse> findById(@PathVariable("id") String id) {
    Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findById(id);

    if (utilisateurOptional.isPresent()) {
        Utilisateur utilisateur = utilisateurOptional.get();
        UtilisateurResponse utilisateurResponse = new UtilisateurResponse();
        BeanUtils.copyProperties(utilisateur, utilisateurResponse);

        List<CompteResponse> comptes = compteFeignClient.getComptesByUtilisateurId(utilisateur.getId());
        List<NoteResponse> notes = noteFeignClient.getNotesByUtilisateurId(utilisateur.getId());

        utilisateurResponse.setComptes(comptes);
        utilisateurResponse.setNotes(notes);

        return ResponseEntity.ok(utilisateurResponse);
    } else {
        return ResponseEntity.notFound().build();
    }
}



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
