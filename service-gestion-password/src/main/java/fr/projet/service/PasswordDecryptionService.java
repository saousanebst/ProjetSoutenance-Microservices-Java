package fr.projet.service;

import java.security.PrivateKey;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import fr.projet.model.AESUtil;
import fr.projet.model.RSAUtil;

import java.sql.Connection;


public class PasswordDecryptionService {

 private PrivateKey privateKey;

    public PasswordDecryptionService(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String retrievePassword(String userId) throws Exception {
        // Récupérer les données chiffrées depuis la base de données
        String encryptedPassword = null;
        String encryptedAESKey = null;
        byte[] ivBytes = null;

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bbcompte")) {
            String querySQL = "SELECT encrypted_password, encrypted_aes_key, iv FROM passwords WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                preparedStatement.setString(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        encryptedPassword = resultSet.getString("encrypted_password");
                        encryptedAESKey = resultSet.getString("encrypted_aes_key");
                        ivBytes = resultSet.getBytes("iv");
                    }
                }
            }
        }

        // Déchiffrer la clé AES avec la clé privée RSA
        SecretKey aesKey = RSAUtil.decryptAESKey(encryptedAESKey, privateKey);

        // Déchiffrer le mot de passe avec AES
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        return AESUtil.decrypt(encryptedPassword, aesKey, iv);
    }
}
