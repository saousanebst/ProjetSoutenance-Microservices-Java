package fr.projet.request;

public class PasswordCheckRequest {
    private String passwordValue;

    public PasswordCheckRequest() {
        // Constructeur par défaut nécessaire pour la désérialisation JSON
    }

    public PasswordCheckRequest(String passwordValue) {
        this.passwordValue = passwordValue;
    }

    public String getPassword() {
        return passwordValue;
    }

    public void setPassword(String passwordValue) {
        this.passwordValue = passwordValue;
    }
}
