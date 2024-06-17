package fr.projet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.util.ReflectionTestUtils;

import fr.projet.Request.PasswordCheckRequest;
import fr.projet.Response.PasswordCheckResponse;
import fr.projet.Response.PasswordGeneratedResponse;
import fr.projet.model.Password;
import fr.projet.model.PasswordResetToken;
import fr.projet.repository.PasswordRepository;
import fr.projet.repository.PasswordResetTokenRepository;

@ExtendWith(MockitoExtension.class) // Intégration de Mockito avec JUnit 5
public class PasswordServiceTest {
    

  @Mock
    private PasswordRepository passwordRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private PasswordService passwordService;


    @Mock
    private JdbcTemplate jdbcTemplate;
    @Test
    public void testCreatePassword_Success() {
        // Données de test
        Password passwordRequest = new Password();
        passwordRequest.setIdUser("omare");
        passwordRequest.setPasswordValue("bastaouiOmar123@");

        // Mock du comportement de passwordRepository.save()
        when(passwordRepository.save(any(Password.class))).thenAnswer(invocation -> {
            Password password = invocation.getArgument(0);
            password.setId("testPasswordId");
            password.setDateAjout(LocalDateTime.now());
            return password;
        });

        // Appel de la méthode à tester
        Password createdPassword = passwordService.createPassword(passwordRequest);

        // Vérification que passwordRepository.save() a été appelé une fois avec les bonnes données
        verify(passwordRepository, times(1)).save(any(Password.class));

        // Vérification du résultat retourné
        assertEquals("testPasswordId", createdPassword.getId());
        assertNotNull(createdPassword.getDateAjout());
    }   


 @Test
    public void testRequestPasswordReset_Success() {
        // Données de test
        String email = "hajar@example.com";

        // Mock du comportement de passwordResetTokenRepository.save()
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> {
            PasswordResetToken resetToken = invocation.getArgument(0);
            resetToken.setId("1L"); // Simule l'attribution d'un ID par le repository
            return resetToken;
        });

        // Appel de la méthode à tester
        passwordService.requestPasswordReset(email);

        // Vérification que passwordResetTokenRepository.save() a été appelé une fois avec les bonnes données
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    public void testGetPasswordByUserId_PasswordFound() throws Exception {
        // Données de test
        String idUser = "sawsan";
        String expectedPasswordValue = "testPassword123";

        // Mock du comportement de findByidUser
        Password password = new Password();
        password.setIdUser(idUser);
        password.setPasswordValue(expectedPasswordValue);
        when(passwordRepository.findByidUser(idUser)).thenReturn(password);

        // Appel de la méthode à tester
        String actualPasswordValue = passwordService.getPasswordByUserId(idUser);

        // Vérification du résultat
        assertEquals(expectedPasswordValue, actualPasswordValue);
    }
  

    @Test
    public void testGetPasswordByUserId_PasswordNotFound() throws Exception {
        // Données de test
        String idUser = "nonExistingUserId";

        // Mock du comportement de findByidUser qui ne trouve pas de mot de passe
        when(passwordRepository.findByidUser(idUser)).thenReturn(null);

        // Appel de la méthode à tester
        String actualPasswordValue = passwordService.getPasswordByUserId(idUser);

        // Vérification du résultat
        assertNull(actualPasswordValue);
    }


    @Test
    public void testUpdatePassword_Success() {
        // Données de test
        String idUser = "fqfqffsx";
        String newPassword = "newPassword123";

        // Mock du comportement de findById pour retourner Optional.empty()
        when(passwordRepository.findById(eq(idUser))).thenReturn(Optional.empty());

        // Appel de la méthode à tester
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            passwordService.updatePassword(idUser, newPassword);
        });

        // Vérification de l'exception lancée
        assertEquals("User not found", exception.getMessage());
    }


 
    @Test
    public void testResetPassword_InvalidToken() {
        // Données de test
        String token = "invalidToken";
        String newPassword = "newPassword12323D@";

        // Mock du comportement de findByToken pour retourner Optional.empty()
        when(passwordResetTokenRepository.findByToken(eq(token))).thenReturn(Optional.empty());

        // Appel de la méthode à tester et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            passwordService.resetPassword(token, newPassword);
        });

        // Vérification du message d'erreur
        assertEquals("Invalid token", exception.getMessage());
    }


@Test
    public void testResetPassword_TokenExpired() {
        // Données de test
        String token = "88e7fa4a-2144-44b0-9777-66ff556af6b4";
        String newPassword = "newPassword123";
        LocalDateTime expiredDate = LocalDateTime.now().minusDays(1); // Date expirée d'il y a un jour

        // Création d'un PasswordResetToken expiré
        PasswordResetToken expiredResetToken = new PasswordResetToken(token, "hicham.db@gmail.com");
        ReflectionTestUtils.setField(expiredResetToken, "expiryDate", expiredDate);

        // Mock du comportement de findByToken pour retourner le token expiré
        when(passwordResetTokenRepository.findByToken(eq(token))).thenReturn(Optional.of(expiredResetToken));

        // Appel de la méthode à tester et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            passwordService.resetPassword(token, newPassword);
        });

        // Vérification du message d'erreur
        assertEquals("Token has expired", exception.getMessage());
    }


@Test
    public void testResetPassword_Success() {
        // Données de test
        String token = "validToken";
        String newPassword = "9mxW1y/bk72";
        String userEmail = "taest@gmail.com";

        // Création d'un PasswordResetToken valide
        PasswordResetToken validResetToken = new PasswordResetToken(token, userEmail);

        // Création d'un Password existant
        Password existingPassword = new Password();
        existingPassword.setPasswordValue("finish");
        existingPassword.setDateModif(LocalDateTime.now().minusDays(1)); // Date de modification d'il y a un jour

        // Mock du comportement de findByToken pour retourner le token valide
        when(passwordResetTokenRepository.findByToken(eq(token))).thenReturn(Optional.of(validResetToken));

        // Mock du comportement de findById pour retourner le Password existant
        when(passwordRepository.findById(eq(userEmail))).thenReturn(Optional.of(existingPassword));

        // Appel de la méthode à tester
        passwordService.resetPassword(token, newPassword);

        // Vérification que le mot de passe a été mis à jour correctement
        //assertEquals(newPassword, existingPassword.getPasswordValue());
      //  assertTrue(existingPassword.getDateModif().isAfter(existingPassword.getDateAjout())); // Vérifie que dateModif > dateAjout
    }

    // Méthode de hashPassword pour le test
    private String hashPassword(String password) {
        // Implémentation factice pour le test
        return "hashedPassword";
    }


    @Test
    public void testCheckPasswordStrength_PasswordStrong() {
        // Création d'une requête avec un mot de passe fort (respectant tous les critères)
        PasswordCheckRequest request = new PasswordCheckRequest("StrongPassword123!");

        // Appel de la méthode à tester
        PasswordCheckResponse response = passwordService.checkPasswordStrength(request);

        // Vérification que la réponse indique que le mot de passe est fort
        assertTrue(response.isStrong());
        assertEquals("Password is strong", response.getMessage());
    }


    @Test
    public void testCheckPasswordStrength_PasswordWeak() {
        // Création d'une requête avec un mot de passe faible (moins de 12 caractères)
        PasswordCheckRequest request = new PasswordCheckRequest("weak");

        // Appel de la méthode à tester
        PasswordCheckResponse response = passwordService.checkPasswordStrength(request);

        // Vérification que la réponse indique que le mot de passe est faible
        assertFalse(response.isStrong());
        assertEquals("Password is weak", response.getMessage());
    }



    @Test
    public void testGeneratePassword() {
        // Appel de la méthode pour générer un mot de passe
        PasswordGeneratedResponse response = passwordService.generatePassword();

        // Vérification que le mot de passe généré n'est ni null ni vide
        assertNotNull(response.getPassword());
        assertFalse(response.getPassword().isEmpty());
    }

    @Test
    public void testCheckPasswordVulnerability_PasswordIsVulnerable() {
        // Simuler un hash de mot de passe vulnérable (similaire à celui stocké dans la base de données)
        String vulnerablePasswordHash = DigestUtils.sha1Hex("E459C969C49BFF2AD4D38C1CF01AC905C2B");
  // Mock pour retourner un count > 0 (mot de passe vulnérable trouvé)
  when(jdbcTemplate.queryForObject(
    eq("SELECT COUNT(*) FROM stolen_password WHERE hash = ?"),
    eq(Integer.class),
    eq(vulnerablePasswordHash)))
    .thenReturn(1);

// Créer une requête avec un mot de passe potentiellement vulnérable
PasswordCheckRequest request = new PasswordCheckRequest("E459C969C49BFF2AD4D38C1CF01AC905C2B");

// Appeler la méthode à tester
PasswordCheckResponse response = passwordService.checkPasswordVulnerability(request);

// Vérifier que la réponse indique que le mot de passe est vulnérable
assertFalse(response.isStrong());
assertEquals("Password is vulnerable", response.getMessage());
}


@Test
    public void testCheckPasswordVulnerability_PasswordIsNotVulnerable() {
        // Simuler un hash de mot de passe non vulnérable
        String safePasswordHash = DigestUtils.sha1Hex("safePassword123!@");

        // Mock pour retourner un count == 0 (mot de passe non vulnérable trouvé)
        when(jdbcTemplate.queryForObject(
                eq("SELECT COUNT(*) FROM stolen_password WHERE hash = ?"),
                eq(Integer.class),
                eq(safePasswordHash)))
                .thenReturn(0);

        // Créer une requête avec un mot de passe potentiellement non vulnérable
        PasswordCheckRequest request = new PasswordCheckRequest("safePassword123!@");

        // Appeler la méthode à tester
        PasswordCheckResponse response = passwordService.checkPasswordVulnerability(request);

        // Vérifier que la réponse indique que le mot de passe n'est pas vulnérable
        assertFalse(response.isStrong());
        assertEquals("Password is not found in the list of stolen passwords", response.getMessage());
    }


 @Test
    public void testGenerateStrongPassword() {
        String generatedPassword = passwordService.generateStrongPassword();

        // Vérifier que la longueur du mot de passe est correcte
        assertEquals(12, generatedPassword.length());

        // Vérifier la présence d'au moins une majuscule, une minuscule, un chiffre et un caractère spécial
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : generatedPassword.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if ("!@#$%^&*()-+".indexOf(c) != -1) {
                hasSpecialChar = true;
            }
        }

        assertTrue(hasUppercase);
        assertTrue(hasLowercase);
        assertTrue(hasDigit);
        assertTrue(hasSpecialChar);

        // Vérifier que le mot de passe est mélangé (différent de l'original)
        assertNotEquals(generatedPassword, passwordService.generateStrongPassword());
    }


}
