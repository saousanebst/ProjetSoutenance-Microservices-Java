package fr.projet.request;

import java.time.LocalDate;

public class UpdateCompteRequest {
    
 
    private String platformName;

    private String platformDescription;

    private LocalDate updateDate;

    private String username;

    private String urlAdress;
    private String password;
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


    


}
