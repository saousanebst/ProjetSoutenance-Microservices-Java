package fr.projet.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.log.LogMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class KafkaUtilisateurConsumer {
    
    @KafkaListener(topics = "utilisateur-logs", groupId = "service-utilisateur")
    public void listen(String message) {
        System.out.println("Received message from Kafka: " + message);
        // Vous pouvez traiter le message reçu ici
    }
}
