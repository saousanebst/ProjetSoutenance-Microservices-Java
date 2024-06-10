package fr.projet.model;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="utilisateur")
public class Utilisateur {
@Id
@UuidGenerator
private String id;
@Column(nullable = false,unique = true)
private String email;
@Column(nullable = false)
private String passwordValue;
@Column(nullable = false)
private String username;
@Column(nullable = false)
private LocalDate birthdate;


public String getEmail() {
    return email;
}
public void setEmail(String email) {
    this.email = email;
}
public String getPassword() {
    return passwordValue;
}
public void setPassword(String password) {
    this.passwordValue = password;
}
public String getUsername() {
    return username;
}
public void setUsername(String username) {
    this.username = username;
}
public LocalDate getBirthdate() {
    return birthdate;
}
public void setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
}


public String getId() {
    return id;
}
public void setId(String id) {
    this.id = id;
}



}
