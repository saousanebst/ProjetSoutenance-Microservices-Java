package fr.projet.model;

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




}
