package fr.projet.service;

import java.util.function.Consumer;


import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
@Service
public class LogConsumer {
     @Bean
    public Consumer<String> logInput() {
        return message -> {
            System.out.println("Consumed log: " + message); // Afficher le message de log consommé
        };
    }
}
