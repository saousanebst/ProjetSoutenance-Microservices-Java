package fr.projet.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.annotation.PostConstruct;

@Configuration
public class ClickHouseConfig {
    
@Autowired

private JdbcTemplate clickHouseJdbcTemplate;


    @PostConstruct
    public void importTxtFiles() {
        File folder = new File("C:/Users/TED/AJC-formation/PROJET-SOUTENANCE/pwnedpasswords");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    importFile(file);
                }
            }
        }
    }

    private void importFile(File file) {
       try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                insertHash(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertHash(String hash) {
        String sql = "INSERT INTO stolen_password (hash) VALUES (?)";
        clickHouseJdbcTemplate.update(sql, hash);
    }


}
