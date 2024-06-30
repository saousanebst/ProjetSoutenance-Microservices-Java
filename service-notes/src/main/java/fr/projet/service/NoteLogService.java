package fr.projet.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class NoteLogService {
     private static final Logger logger = LoggerFactory.getLogger(NoteLogService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StreamBridge streamBridge;

    @Autowired
    public NoteLogService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    private String formatMessage(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        return String.format("%s [%s]: %s", timestamp, level, message);
    }

    public void sendLog(String level, String message) {
        String formattedMessage = formatMessage(level, message);
        logger.info("Sending log message: {}", formattedMessage);
        streamBridge.send("logOutput-out-0", formattedMessage);
    }

    public void logInfo(String message) {
        sendLog("INFO", message);
    }

    public void logDebug(String message) {
        sendLog("DEBUG", message);
    }

    public void logError(String message) {
        sendLog("ERROR", message);
    }
}
