package fr.projet.request;

import java.time.LocalDate;

public class CreateNoteRequest {
    

private String nom;
private String description;
private LocalDate dateAjout;
private LocalDate dateModif;
private String contenu;
private String idUser;
public String getNom() {
    return nom;
}

public String getIdUser() {
    return idUser;
}

public void setIdUser(String idUser) {
    this.idUser = idUser;
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




}
