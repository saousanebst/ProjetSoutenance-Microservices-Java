package fr.projet.model;

import java.time.LocalDate;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
@Entity
@Table(name= "note")
public class Note {
@Id
@UuidGenerator
private String id;
private String nom;
private String description;
private LocalDate dateAjout;
private LocalDate dateModif;


private String contenu;
private String idUser;


private String publicKey;


public String getNom() {
    return nom;
}

public void setNom(String nom) {
    this.nom = nom;
}

public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}

public LocalDate getDateAjout() {
    return dateAjout;
}

public void setDateAjout(LocalDate dateAjout) {
    this.dateAjout = dateAjout;
}

public LocalDate getDateModif() {
    return dateModif;
}

public void setDateModif(LocalDate dateModif) {
    this.dateModif = dateModif;
}

public String getContenu() {
    return contenu;
}

public void setContenu(String contenu) {
    this.contenu = contenu;
}

public String getId() {
    return id;
}

public void setId(String id) {
    this.id = id;
}

public String getIdUser() {
    return idUser;
}

public void setIdUser(String idUser) {
    this.idUser = idUser;
}

public String getPublicKey() {
    return publicKey;
}

public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
}



}
