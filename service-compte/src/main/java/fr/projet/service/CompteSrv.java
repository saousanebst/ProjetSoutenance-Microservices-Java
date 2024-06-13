package fr.projet.service;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.model.Compte;
import fr.projet.model.PrivateKey;
import fr.projet.repository.CompteRepository;
import fr.projet.repository.PrivateKeyRepository;

@Service
public class CompteSrv {


    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private PrivateKeyRepository privateKeyRepository;

//update id
 
     public Compte update(Compte compte) {
        Optional<Compte> Compte = compteRepository.findById(compte.getId());
        if (Compte.isPresent()) {
            Compte updatedCompte = Compte.get();
            updatedCompte.setPlatformName(compte.getPlatformName());
            updatedCompte.setPlatformDescription(compte.getPlatformDescription());
            updatedCompte.setUsername(compte.getUsername());
            updatedCompte.setUrlAdress(compte.getUrlAdress());
            updatedCompte.setPassword(compte.getPassword());
            updatedCompte.setUpdateDate(LocalDate.now());
            return compteRepository.save(updatedCompte);
        } else {
            throw new RuntimeException("Compte not found with id " + compte.getId());
        }
    }



//delete

public void deleteCompteById (String id){
    this.compteRepository.deleteById(id);
}


//findById

public Compte findById(String id) {
        Optional<Compte> compte = compteRepository.findById(id);
        if (compte.isPresent()) {
            return compte.get();
        } else {
            throw new RuntimeException("Compte not found with id " + id);
        }
    }

public PrivateKey findByCompteId(String compteId){

    Optional<PrivateKey> privateKey = privateKeyRepository.findByCompteId(compteId);
    if (privateKey.isPresent()) {
        return privateKey.get();
    } else {
        throw new RuntimeException(" key not found with id " + privateKey);
    }

}



}