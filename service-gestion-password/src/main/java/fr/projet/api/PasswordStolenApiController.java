package fr.projet.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/passwordstolen")
@CrossOrigin("*")
public class PasswordStolenApiController {

private static final Logger log = LoggerFactory.getLogger(PasswordStolenApiController.class);



    @PostMapping
    public void readAndSaveFromDirectory() {
        File directory = new File("C:/Users/TED/AJC-formation/PROJET-SOUTENANCE/pwnedpasswords"); // Spécifiez le chemin de votre répertoire local
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt")); // Récupère tous les fichiers .txt du répertoire

        if (files != null) {
            for (File file : files) {
                readAndSave(file);
            }
        } else {
            log.error("Le répertoire spécifié est invalide ou inaccessible.");
        }
    }

    private void readAndSave(File file) {
        Set<String> hashedPasswords = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":"); // Supposons que le mot de passe haché est séparé par un espace
                String hashedPassword = parts[0]; // Supposons que le mot de passe haché est la première partie de la ligne

                hashedPasswords.add(hashedPassword);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Problème lors de la lecture du fichier " + file.getName());
            return; // Arrêtez le traitement si une erreur se produit lors de la lecture du fichier
        }

        // Stocker les mots de passe hachés dans la base de données
        storeHashedPasswords(hashedPasswords);
    }

    private void storeHashedPasswords(Set<String> hashedPasswords) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/stolen_passwords", "postgres", "root")) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO stolen_password (hash) VALUES (?)")) {
                for (String hashedPassword : hashedPasswords) {
                    statement.setString(1, hashedPassword);
                    statement.addBatch();
                }

                statement.executeBatch();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Problème lors de la connexion à la base de données ou lors de l'insertion des mots de passe hachés.");
        }
    }




}
