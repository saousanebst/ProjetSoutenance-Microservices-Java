package fr.projet.response;

import java.time.LocalDate;


public class CompteResponse {

private String id;
private String platformName;
private String platformDescription;
private LocalDate creationDate;
private LocalDate updateDate;
private String username;
private String urlAdress;
private String password;
private String email;


public String getId() {
    return id;
}
public void setId(String id) {
    this.id = id;
}
public String getPlatformName() {
    return platformName;
}
public void setPlatformName(String platformName) {
    this.platformName = platformName;
}
public String getPlatformDescription() {
    return platformDescription;
}
public void setPlatformDescription(String platformDescription) {
    this.platformDescription = platformDescription;
}
public LocalDate getCreationDate() {
    return creationDate;
}
public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
}
public LocalDate getUpdateDate() {
    return updateDate;
}
public void setUpdateDate(LocalDate updateDate) {
    this.updateDate = updateDate;
}
public String getUsername() {
    return username;
}
public void setUsername(String username) {
    this.username = username;
}
public String getUrlAdress() {
    return urlAdress;
}
public void setUrlAdress(String urlAdress) {
    this.urlAdress = urlAdress;
}
public String getPassword() {
    return password;
}
public void setPassword(String password) {
    this.password = password;
}
public String getEmail() {
    return email;
}
public void setEmail(String email) {
    this.email = email;
}

  



}
