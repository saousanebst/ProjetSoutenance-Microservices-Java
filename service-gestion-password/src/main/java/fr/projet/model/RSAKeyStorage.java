package fr.projet.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class RSAKeyStorage {

     @Autowired
    private JdbcTemplate jdbcTemplate;

    public void storePrivateKey(String privateKey) {
        String sql = "INSERT INTO rsa_keys (private_key) VALUES (?)";
        jdbcTemplate.update(sql, privateKey);
    }

    public String getPrivateKey() {
        String sql = "SELECT private_key FROM rsa_keys LIMIT 1";
        return jdbcTemplate.queryForObject(sql, String.class);
    }
    
}
