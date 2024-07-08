package fr.projet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.projet.service.CryptographService;


class ServiceCompteApplicationTests {
@Autowired

CryptographService cryptographService;
	



    @BeforeEach
    public void setUp() {
        cryptographService = new CryptographService();
        cryptographService.init();
    }

    @Test
    public void testDecryptPassword() throws Exception {
        // Générer une paire de clés RSA
        KeyPair keyPair = cryptographService.generateKeyPair();
        String privateKeyStr = cryptographService.encodePrivateKey(keyPair.getPrivate());
        String publicKeyStr = cryptographService.encodePublicKey(keyPair.getPublic());

        // Mot de passe à chiffrer et déchiffrer
        String originalPassword = "$2a$10$ye3yBw1/c1fLKwbHvQOEAuAgSDon3I2LPx1pOThUPrBKqQ.Ga2GN2";

        // Chiffrer le mot de passe avec la clé publique
        String encryptedPassword = encryptPasswordWithPublicKey(originalPassword, publicKeyStr);

        // Déchiffrer le mot de passe avec la clé privée
        String actualDecryptedPassword = cryptographService.decryptPassword(encryptedPassword, privateKeyStr);

        // Vérifier que le mot de passe déchiffré correspond au mot de passe original
        assertEquals(originalPassword, actualDecryptedPassword);
    }

    // Méthode auxiliaire pour chiffrer un mot de passe avec une clé publique
    private String encryptPasswordWithPublicKey(String password, String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedPasswordBytes = cipher.doFinal(password.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(encryptedPasswordBytes);
    }

}