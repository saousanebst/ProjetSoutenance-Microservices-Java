package fr.projet.api.dto;

public class ConnexionDTO {

    private String email;
    private String password;

public ConnexionDTO(){
    super();
}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    
}
