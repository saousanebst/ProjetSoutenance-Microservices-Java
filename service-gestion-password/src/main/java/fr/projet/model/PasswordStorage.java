package fr.projet.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

//Insérez la clé publique et les mots de passe chiffrés dans la table

public class PasswordStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void storePassword(String password, String publicKey) {
        String sql = "INSERT INTO compte (password, public_key) VALUES (?, ?)";
        jdbcTemplate.update(sql,password, publicKey);
    }

    public String getPublicKey() {
        String sql = "SELECT public_key FROM compte LIMIT 1";
        return jdbcTemplate.queryForObject(sql, String.class);
    }
}
