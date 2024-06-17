package fr.projet.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table
public class PasswordResetToken {
    @Id
    @UuidGenerator
    private String id;
     private String token;
    private String email;
    private LocalDateTime expiryDate;

    public PasswordResetToken(){ }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    
    

}
