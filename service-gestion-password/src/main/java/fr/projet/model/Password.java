package fr.projet.model;


import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="password")
public class Password {
@Id
@UuidGenerator
private String Id;

private String idUser;

private String passwordValue;

private LocalDateTime DateAjout;

private LocalDateTime DateModif;

public String getId() {
    return Id;
}

public void setId(String id) {
    Id = id;
}

public String getIdUser() {
    return idUser;
}

public void setIdUser(String idUser) {
    this.idUser = idUser;
}

public LocalDateTime getDateAjout() {
    return DateAjout;
}

public void setDateAjout(LocalDateTime dateAjout) {
    DateAjout = dateAjout;
}

public LocalDateTime getDateModif() {
    return DateModif;
}

public void setDateModif(LocalDateTime dateModif) {
    DateModif = dateModif;
}

public String getPasswordValue() {
    return passwordValue;
}

public void setPasswordValue(String passwordValue) {
    this.passwordValue = passwordValue;
}




}
