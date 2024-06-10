package fr.projet.response;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;
@Component
public class UtilisateurResponse {
    
private String id ;

private String email;

private String passwordValue;

private String username;

private LocalDate birthdate;

private List<CompteResponse> comptes;

private List<NoteResponse> notes;





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

public List<CompteResponse> getComptes() {
    return comptes;
}

public void setComptes(List<CompteResponse> comptes) {
    this.comptes = comptes;
}

public List<NoteResponse> getNotes() {
    return notes;
}

public void setNotes(List<NoteResponse> notes) {
    this.notes = notes;
}

public String getPasswordValue() {
    return passwordValue;
}

public void setPasswordValue(String passwordValue) {
    this.passwordValue = passwordValue;
}






}
