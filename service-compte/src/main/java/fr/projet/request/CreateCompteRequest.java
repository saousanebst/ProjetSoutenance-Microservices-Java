package fr.projet.request;

import java.time.LocalDate;


public class CreateCompteRequest {
    
    private String platformName;

    private String platformDescription;

    private LocalDate creationDate;

    

    private String username;

    private String urlAdress;
    private String password;

    private String UserId;
    

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
    public String getUserId() {
        return UserId;
    }
    public void setUserId(String userId) {
        UserId = userId;
    }
  


}
