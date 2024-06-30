package fr.projet.response;

public class PasswordCheckResponse {
    private boolean strong;
    private boolean vulnerable;
    private String message;


    public PasswordCheckResponse() {
        // Constructeur par défaut nécessaire pour la désérialisation JSON
    }

    public PasswordCheckResponse(boolean strong, boolean vulnerable, String message) {
        this.strong = strong;
        this.vulnerable = vulnerable;
        this.message = message;
    }

    public boolean isStrong() {
        return strong;
    }

    public void setStrong(boolean strong) {
        this.strong = strong;
    }

    public boolean isVulnerable() {
        return vulnerable;
    }

    public void setVulnerable(boolean vulnerable) {
        this.vulnerable = vulnerable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }












}
