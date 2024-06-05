package fr.projet.request;

import java.time.LocalDate;


public class CreateCompteRequest {
    
    private String platformName;

    private String platformDescription;

    private LocalDate creationDate;

    private LocalDate updateDate;

    private String username;

    private String urlAdress;
    private String passwordPlatform;

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
    public String getPasswordPlatform() {
        return passwordPlatform;
    }
    public void setPasswordPlatform(String passwordPlatform) {
        this.passwordPlatform = passwordPlatform;
    }





}
