package fr.projet.api.dto;

public class ConnexionDTO {

    private String email;
    private String passwordValue;

public ConnexionDTO(){
    super();
}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordValue() {
        return passwordValue;
    }

    public void setPasswordValue(String passwordValue) {
        this.passwordValue = passwordValue;
    }
  
    
}
