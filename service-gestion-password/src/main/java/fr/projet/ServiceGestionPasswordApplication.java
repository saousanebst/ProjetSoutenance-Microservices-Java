package fr.projet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceGestionPasswordApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ServiceGestionPasswordApplication.class, args);

		// KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // keyPairGenerator.initialize(2048);
        // KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // PublicKey publicKey = keyPair.getPublic();
        // PrivateKey privateKey = keyPair.getPrivate();

        // String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        // System.out.println("Public Key: " + publicKeyBase64);
        // System.out.println("Private Key: " + privateKeyBase64);
    }
	}


	

