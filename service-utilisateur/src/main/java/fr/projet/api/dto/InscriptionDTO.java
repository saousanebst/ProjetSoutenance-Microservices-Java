package fr.projet.api.dto;

import java.time.LocalDate;

public class InscriptionDTO {
    
private String email;
private String password;
private String username;
private LocalDate birthdate; 

public InscriptionDTO() {
    super();
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
