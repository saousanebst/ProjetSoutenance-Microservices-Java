package fr.projet.service;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import fr.projet.model.AESUtil;
import fr.projet.model.RSAUtil;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class PasswordStorageService {

    private PublicKey publicKey;

    public PasswordStorageService(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void storePassword(String plainPassword) throws Exception {
        // Générer une clé AES
        SecretKey aesKey = AESUtil.generateAESKey();

        // Générer un IV (initialization vector) pour AES
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[16];
        secureRandom.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // Chiffrer le mot de passe avec AES
        String encryptedPassword = AESUtil.encrypt(plainPassword, aesKey, iv);

        // Chiffrer la clé AES avec RSA
        String encryptedAESKey = RSAUtil.encryptAESKey(aesKey, publicKey);

        // Stocker le mot de passe chiffré, la clé AES chiffrée et l'IV dans la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:postgres://localhost:5432/bddcompte")) {
            String insertSQL = "INSERT INTO compte (encrypted_password, encrypted_aes_key) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, encryptedPassword);
                preparedStatement.setString(2, encryptedAESKey);
                preparedStatement.executeUpdate();
            }
        }
    }
}