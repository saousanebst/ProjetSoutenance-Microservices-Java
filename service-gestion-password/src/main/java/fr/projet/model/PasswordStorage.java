package fr.projet.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class PasswordStorage {
    @Autowired
    private JdbcTemplate clickHouseJdbcTemplate;

    public void storePassword(int id, String encryptedPassword, String publicKey) {
        String sql = "INSERT INTO passwords (id, encrypted_password, public_key) VALUES (?, ?, ?)";
        clickHouseJdbcTemplate.update(sql, id, encryptedPassword, publicKey);
    }

    public String getPublicKey() {
        String sql = "SELECT public_key FROM passwords LIMIT 1";
        return clickHouseJdbcTemplate.queryForObject(sql, String.class);
    }
}
