package fr.projet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;

import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public class NoteServiceCryptoTest {
    

private CryptoService cryptoService;
    private KeyPair keyPair;
    private String contentToEncrypt = "Hello, this is a secret message.";

    @BeforeEach
    public void setup() throws Exception {
        cryptoService = new CryptoService();
        cryptoService.init(); // Initialize Bouncy Castle provider

        keyPair = cryptoService.generateKeyPair();
    }

    @Test
    public void testEncodePublicKey() {
        // Appel de la méthode à tester
        String publicKeyStr = cryptoService.encodePublicKey(keyPair.getPublic());

        // Vérification que la chaîne encodée n'est pas nulle
        assertNotNull(publicKeyStr);
        assertTrue(publicKeyStr.startsWith("MIIBIjANBgkqhkiG"));
    }

    @Test
    public void testEncodePrivateKey() {
        // Appel de la méthode à tester
        String privateKeyStr = cryptoService.encodePrivateKey(keyPair.getPrivate());

        // Vérification que la chaîne encodée n'est pas nulle
        assertNotNull(privateKeyStr);
        //assertTrue(privateKeyStr.startsWith("MIIEvgIBADANBgkqhkiG"));
    }

 @Test
    public void testEncryptAndDecryptWithKeys() throws Exception {
        // Chiffrement avec la clé publique
        String encryptedContent = cryptoService.encryptNoteWithPublicKey(contentToEncrypt, cryptoService.encodePublicKey(keyPair.getPublic()));
        assertNotNull(encryptedContent);

        // Déchiffrement avec la clé privée
        String decryptedContent = cryptoService.decryptNoteWithPrivateKey(encryptedContent, cryptoService.encodePrivateKey(keyPair.getPrivate()));
        assertNotNull(decryptedContent);

        // Vérification que le contenu déchiffré correspond au contenu original
        assertEquals(contentToEncrypt, decryptedContent);
    }


     @Test
    public void testEncryptAndDecryptWithInvalidKeys() throws Exception {
        // Clés différentes
        KeyPair anotherKeyPair = cryptoService.generateKeyPair();

        // Chiffrement avec la clé publique
        String encryptedContent = cryptoService.encryptNoteWithPublicKey(contentToEncrypt, cryptoService.encodePublicKey(keyPair.getPublic()));
        assertNotNull(encryptedContent);

       // Tentative de déchiffrement avec une autre clé privée
        Exception exception = assertThrows(Exception.class, () -> {
            cryptoService.decryptNoteWithPrivateKey(encryptedContent, cryptoService.encodePrivateKey(anotherKeyPair.getPrivate()));
        });

        // Vérification de l'exception lancée
        assertTrue(exception instanceof BadBlockException || exception.getCause() instanceof BadBlockException);
    }











}
