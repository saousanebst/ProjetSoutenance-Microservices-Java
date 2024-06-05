package fr.projet.response;

import java.time.LocalDate;

import org.springframework.stereotype.Component;
@Component
public class UtilisateurResponse {
    
private String id ;

private String email;

private String password;

private String username;

private LocalDate birthdate;

private String idCompte;





public String getIdCompte() {
    return idCompte;
}

public void setIdCompte(String idCompte) {
    this.idCompte = idCompte;
}

public String getId() {
    return id;
}

public void setId(String id) {
    this.id = id;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
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






}
