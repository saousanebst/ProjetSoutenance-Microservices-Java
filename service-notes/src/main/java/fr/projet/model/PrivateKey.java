package fr.projet.model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;


@Entity
@Table(name = "private_key")
public class PrivateKey {

    

    @Id
    @UuidGenerator
    private String id;
    private String noteId;

   
    private String privateKey;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNoteId() {
        return noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    
    
}
