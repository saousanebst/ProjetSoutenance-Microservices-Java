package fr.projet.Response;

import java.time.LocalDateTime;

public class PasswordResponse {
    
   
    private String userId;
    private String platformName;
    private String username;
    private String passwordValue;
    // private LocalDateTime dateAjout;
    // private LocalDateTime dateModif;


   
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPlatformName() {
        return platformName;
    }
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPasswordValue() {
        return passwordValue;
    }
    public void setPasswordValue(String passwordValue) {
        this.passwordValue = passwordValue;
    }
    // public LocalDateTime getDateAjout() {
    //     return dateAjout;
    // }
    // public void setDateAjout(LocalDateTime dateAjout) {
    //     this.dateAjout = dateAjout;
    // }
    // public LocalDateTime getDateModif() {
    //     return dateModif;
    // }
    // public void setDateModif(LocalDateTime dateModif) {
    //     this.dateModif = dateModif;
    // }

    
}
