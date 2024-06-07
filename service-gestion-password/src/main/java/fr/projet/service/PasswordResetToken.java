package fr.projet.service;

import java.time.LocalDateTime;

public class PasswordResetToken {

     private String token;
    private String email;
    private LocalDateTime expiryDate;


    public PasswordResetToken(String token, String email) {
        this.token = token;
        this.email = email;
        // Définir une date d'expiration par exemple une heure plus tard
        this.expiryDate = LocalDateTime.now().plusHours(1);
    }

    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }


    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }


    
    

}
