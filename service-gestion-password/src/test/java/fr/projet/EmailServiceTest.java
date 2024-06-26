package fr.projet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

@SpringBootTest
public class EmailServiceTest {
     @Autowired
    private JavaMailSender javaMailSender;

    @Test
    public void testSendEmail() {
        assertDoesNotThrow(() -> {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("bastaoui.saousane@gmail.com");
            helper.setSubject("Test Email");
            helper.setText("This is a test email.");
            javaMailSender.send(message);
        });
    }
}
