package fr.projet.response;

public class PasswordCheckResponse {


    private boolean isStrong;
    private boolean isVulnerable;
    private String message;

    public PasswordCheckResponse() {}

    public PasswordCheckResponse(boolean isStrong, boolean isVulnerable, String message) {
        this.isStrong = isStrong;
        this.isVulnerable = isVulnerable;
        this.message = message;
    }

    public boolean isStrong() {
        return isStrong;
    }

    public void setStrong(boolean isStrong) {
        this.isStrong = isStrong;
    }

    public boolean isVulnerable() {
        return isVulnerable;
    }

    public void setVulnerable(boolean isVulnerable) {
        this.isVulnerable = isVulnerable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    

}
