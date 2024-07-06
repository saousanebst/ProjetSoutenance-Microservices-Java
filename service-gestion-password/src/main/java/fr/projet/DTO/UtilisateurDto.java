package fr.projet.DTO;

public class UtilisateurDto {
    private String email;
    private String id;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public UtilisateurDto(String email, String id) {
        this.email = email;
        this.id = id;
    }
    public UtilisateurDto() {
    }




    
}
