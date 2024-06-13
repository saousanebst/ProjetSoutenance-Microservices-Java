package fr.projet.model;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class PrivateKey {
    

    @Id
    @UuidGenerator // Utilisation d'une stratégie d'identification automatique

    private String id;
    private String compteId;
    private String privateKey;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCompteId() {
        return compteId;
    }
    public void setCompteId(String compteId) {
        this.compteId = compteId;
    }
    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
