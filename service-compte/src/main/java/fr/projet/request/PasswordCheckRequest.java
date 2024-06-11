package fr.projet.request;

public class PasswordCheckRequest {

    private String password;

    public PasswordCheckRequest(){}

    public PasswordCheckRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

  

}
