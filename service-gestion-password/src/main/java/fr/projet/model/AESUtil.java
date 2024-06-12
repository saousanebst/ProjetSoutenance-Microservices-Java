package fr.projet.model;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {


    public static String encryptPassword(String password, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static String encryptAESKey(SecretKey secretKey, PublicKey publicKey) throws Exception {
        String aesKeyBase64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        return RSAUtil.encryptWithPublicKey(aesKeyBase64, publicKey);
    }
    

     public static String decryptPassword(String encryptedPassword, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedData);
    }

    public static SecretKey decryptAESKey(String encryptedAESKey, PrivateKey privateKey) throws Exception {
        String aesKeyBase64 = RSAUtil.decryptWithPrivateKey(encryptedAESKey, privateKey);
        byte[] decodedKey = Base64.getDecoder().decode(aesKeyBase64);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
