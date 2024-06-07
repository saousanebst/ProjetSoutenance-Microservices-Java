package fr.projet.Request;
import jakarta.validation.constraints.NotBlank;


public class CreatePasswordRequest {


    @NotBlank
    private String userId;

  
    @NotBlank
    private String platformName;

    private String username;


   
    @NotBlank 
    private String passwordValue;


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



}
