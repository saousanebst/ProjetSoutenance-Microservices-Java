package fr.projet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;


public class CryptographieServiceTest {
    
    private CryptographService cryptographService;

    @BeforeEach
    void setUp() {
        cryptographService = new CryptographService();
        cryptographService.init(); // Initialiser le fournisseur de sécurité
    }

    @Test
    void testGenerateKeyPair() throws Exception {
        KeyPair keyPair = cryptographService.generateKeyPair();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPrivate());
        assertNotNull(keyPair.getPublic());
    }

    @Test
    void testEncryptAndDecryptPassword() throws Exception {
        String password = "mySecretPassword";

        // Générer une paire de clés
        KeyPair keyPair = cryptographService.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Encoder les clés
        String publicKeyStr = cryptographService.encodePublicKey(publicKey);
        String privateKeyStr = cryptographService.encodePrivateKey(privateKey);

        // Chiffrer le mot de passe
        String encryptedPassword = cryptographService.encryptPasswordWithPublicKey(password, publicKeyStr);
        assertNotNull(encryptedPassword);

        // Déchiffrer le mot de passe
        String decryptedPassword = cryptographService.decryptPassword(encryptedPassword, privateKeyStr);
        assertEquals(password, decryptedPassword);
    }

    @Test
    void testEncodePublicKey() throws Exception {
        KeyPair keyPair = cryptographService.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();

        String encodedPublicKey = cryptographService.encodePublicKey(publicKey);
        assertNotNull(encodedPublicKey);

        // Décoder la clé pour vérifier l'intégrité
        byte[] decodedPublicKeyBytes = Base64.getDecoder().decode(encodedPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey decodedPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodedPublicKeyBytes));
        assertEquals(publicKey, decodedPublicKey);
    }

    @Test
    void testEncodePrivateKey() throws Exception {
        KeyPair keyPair = cryptographService.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        String encodedPrivateKey = cryptographService.encodePrivateKey(privateKey);
        assertNotNull(encodedPrivateKey);

        // Décoder la clé pour vérifier l'intégrité
        byte[] decodedPrivateKeyBytes = Base64.getDecoder().decode(encodedPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey decodedPrivateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedPrivateKeyBytes));
        assertEquals(privateKey, decodedPrivateKey);
    }
}
