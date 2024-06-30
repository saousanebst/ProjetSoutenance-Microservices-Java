package fr.projet.response;

public class PasswordGeneratedResponse {
    private String password;

    public PasswordGeneratedResponse() {
        // Constructeur par défaut nécessaire pour la désérialisation JSON
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
