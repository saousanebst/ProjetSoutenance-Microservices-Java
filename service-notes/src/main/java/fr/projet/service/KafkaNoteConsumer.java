package fr.projet.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class KafkaNoteConsumer {
     @KafkaListener(topics = "note-logs", groupId = "service-notes")
    public void listen(String message) {
        System.out.println("Received message from Kafka: " + message);
        // Vous pouvez traiter le message reçu ici
    }
}
