package fr.projet.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.security.Security;

@Service
public class CryptoService {
    

    @PostConstruct
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public String encodePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String encodePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public String encryptNoteWithPublicKey(String content, String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptNoteWithPrivateKey(String encryptedContent, String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public String decryptNote(String encryptedContent, String privateKeyStr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'decryptNote'");
    }
}