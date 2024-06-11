package fr.projet.Request;

public class PasswordCheckRequest {

    private String passwordPlatform;

    public PasswordCheckRequest(){}

    public PasswordCheckRequest(String passwordPlatform) {
        this.passwordPlatform = passwordPlatform;
    }

    public String getPasswordPlatform() {
        return passwordPlatform;
    }

    public void setPasswordPlatform(String passwordPlatform) {
        this.passwordPlatform = passwordPlatform;
    }


    

}
