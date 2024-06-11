package fr.projet.response;

public class PasswordGeneratedResponse {


    private String password;

    
    public PasswordGeneratedResponse() {
    }


    public PasswordGeneratedResponse(String password) {
        this.password = password;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    
}
