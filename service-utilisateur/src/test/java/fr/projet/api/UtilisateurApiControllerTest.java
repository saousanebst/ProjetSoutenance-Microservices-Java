
package fr.projet.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.projet.api.dto.ConnexionDTO;
import fr.projet.api.dto.InscriptionDTO;
import fr.projet.feignClient.CompteFeignClient;
import fr.projet.feignClient.NoteFeignClient;
import fr.projet.feignClient.PasswordFeignClient;
import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;
import fr.projet.response.CompteResponse;
import fr.projet.response.NoteResponse;
import fr.projet.response.PasswordCheckResponse;
import fr.projet.response.UtilisateurResponse;
import fr.projet.service.UtilisateurLogService;
import fr.projet.service.UtilisateurService;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class UtilisateurApiControllerTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;
 @Mock
    private UtilisateurService utilisateurService;
    @Mock
    private CompteFeignClient compteFeignClient;

    @Mock
    private NoteFeignClient noteFeignClient;

    @Mock
    private PasswordFeignClient passwordFeignClient;
    @Mock
    private UtilisateurLogService logService;

    @InjectMocks
    private UtilisateurApiController utilisateurController;

    private MockMvc mockMvc;

    private List<Utilisateur> mockUtilisateurs;

    @BeforeEach
    public void setUp() {
        
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

        mockUtilisateurs = new ArrayList<>();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("sawsan");
     
        mockUtilisateurs.add(utilisateur);

        when(utilisateurRepository.findAll()).thenReturn(mockUtilisateurs);
        when(utilisateurRepository.findById("sawsan")).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.findByEmail("sara@example.com")).thenReturn(Optional.of(utilisateur));

    }

    @Test
    public void testFindAll() {
        List<CompteResponse> mockComptes = new ArrayList<>();
        List<NoteResponse> mockNotes = new ArrayList<>();
        String mockPassword = "111111";

        when(compteFeignClient.getComptesByUtilisateurId("sawsan")).thenReturn(mockComptes);
        when(noteFeignClient.getNotesByUtilisateurId("sawsan")).thenReturn(mockNotes);
        when(passwordFeignClient.getPasswordByUserId("sawsan")).thenReturn(mockPassword);

        List<UtilisateurResponse> response = utilisateurController.findAll();

        assertNotNull(response);
        assertEquals(1, response.size());

        UtilisateurResponse utilisateurResponse = response.get(0);
        assertEquals(mockComptes, utilisateurResponse.getComptes());
        assertEquals(mockNotes, utilisateurResponse.getNotes());
        assertEquals(mockPassword, utilisateurResponse.getPasswordValue());
    }

    @Test
    public void testFindById_UserFound() {
        // Mock des réponses des clients Feign et du repository
        List<CompteResponse> mockComptes = new ArrayList<>();
        List<NoteResponse> mockNotes = new ArrayList<>();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("sawsan");
        when(utilisateurRepository.findById("sawsan")).thenReturn(Optional.of(utilisateur));
        when(compteFeignClient.getComptesByUtilisateurId("sawsan")).thenReturn(mockComptes);
        when(noteFeignClient.getNotesByUtilisateurId("sawsan")).thenReturn(mockNotes);

        // Appel à la méthode du contrôleur pour trouver l'utilisateur par ID
        ResponseEntity<UtilisateurResponse> responseEntity = utilisateurController.findById("sawsan");

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        UtilisateurResponse utilisateurResponse = responseEntity.getBody();
        assertNotNull(utilisateurResponse);
        assertEquals(mockComptes, utilisateurResponse.getComptes());
        assertEquals(mockNotes, utilisateurResponse.getNotes());

        // Vérification des logs avec le mock logService
        verify(logService, times(1)).logInfo("Fetching user details for ID: sawsan");
    }
    @Test
    public void testInscription_TodayBirthdate() throws Exception {
        InscriptionDTO inscriptionDTO = new InscriptionDTO();
        inscriptionDTO.setEmail("sara@example.com");
        inscriptionDTO.setUsername("sawsana");
        inscriptionDTO.setPasswordValue("123456");
        inscriptionDTO.setBirthdate(LocalDate.now()); // Date de naissance égale à la date du jour
    
        mockMvc.perform(post("/api/utilisateur/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(inscriptionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("La date de naissance ne peut pas être aujourd'hui.")));
    }
    

    @Test
    public void testFindById_UserNotFound() {
        when(utilisateurRepository.findById("TestUsernotFouned")).thenReturn(Optional.empty());

        ResponseEntity<UtilisateurResponse> responseEntity = utilisateurController.findById("TestUsernotFouned");

        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.notFound().build().getStatusCode(), responseEntity.getStatusCode());
    }


    @Test
    public void testConnexion_Success() throws Exception {
        // Créez un objet ConnexionDTO avec les données de test
        ConnexionDTO connexionDTO = new ConnexionDTO();
        connexionDTO.setEmail("sara@example.com");
        connexionDTO.setPasswordValue("123456");

        // Créez un Utilisateur fictif pour la vérification
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("sara@example.com");
        utilisateur.setPassword("123456");

        // Configurez le mock du repository pour simuler la recherche par email
        when(utilisateurRepository.findByEmail("sara@example.com")).thenReturn(Optional.of(utilisateur));

        // Initialisez le mockMvc pour le contrôleur
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

        // Effectuez la requête POST vers /api/utilisateur/connexion
        mockMvc.perform(post("/api/utilisateur/connexion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(connexionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }



    @Test
    public void testConnexion_InvalidPassword() throws Exception {
        // Créez un objet ConnexionDTO avec un email existant mais un mot de passe incorrect
        ConnexionDTO connexionDTO = new ConnexionDTO();
        connexionDTO.setEmail("sara@example.com");
        connexionDTO.setPasswordValue("motdepasseincorrect");

        // Créez un Utilisateur fictif pour la vérification
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("sara@example.com");
        utilisateur.setPassword("123456");

        // Configurez le mock du repository pour simuler la recherche par email
        when(utilisateurRepository.findByEmail("sara@example.com")).thenReturn(Optional.of(utilisateur));

        // Initialisez le mockMvc pour le contrôleur
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

        // Effectuez la requête POST vers /api/utilisateur/connexion
        mockMvc.perform(post("/api/utilisateur/connexion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(connexionDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Le mot de passe est incorrect")); // Vérifie le message d'erreur
    }
    @Test
    public void testConnexion_UserNotFound() throws Exception {
        ConnexionDTO connexionDTO = new ConnexionDTO();
        connexionDTO.setEmail("testAnonyme@example.com");
        connexionDTO.setPasswordValue("password123");

        when(utilisateurRepository.findByEmailAndPasswordValue("testAnonyme@example.com", "password123"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/utilisateur/connexion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(connexionDTO)))
                .andExpect(status().isNotFound());
    }

//    @Test
// public void testInscription_Success() throws Exception {
//    // Créez un objet InscriptionDTO avec les données de test valides
//    InscriptionDTO inscriptionDTO = new InscriptionDTO();
//    inscriptionDTO.setEmail("sarati@example.com");
//    inscriptionDTO.setUsername("sawsana");
//    inscriptionDTO.setPasswordValue("123456Hiyhs@fdf");
//    inscriptionDTO.setBirthdate(LocalDate.of(1998, 3, 5)); // Date de naissance valide

//    // Simulez que l'e-mail n'existe pas déjà dans la base de données
//    when(utilisateurRepository.existsByEmail("sarati@example.com")).thenReturn(false);

//    // Simulez la réponse de Feign Client pour la vérification de vulnérabilité du mot de passe
//    PasswordCheckResponse vulnerabilityResponse = new PasswordCheckResponse(false, true, "Le mot de passe est vulnérable");
//    when(passwordFeignClient.checkPasswordVulnerability(any())).thenReturn(vulnerabilityResponse);

//    // Simulez la réponse de Feign Client pour la vérification de la force du mot de passe
//    PasswordCheckResponse strengthResponse = new PasswordCheckResponse(true, true, "Le mot de passe est suffisamment fort");
//    when(passwordFeignClient.checkPasswordStrength(any())).thenReturn(strengthResponse);

//    // Simulez l'enregistrement de l'utilisateur dans la base de données
//    Utilisateur utilisateur = new Utilisateur();
//    utilisateur.setEmail(inscriptionDTO.getEmail());
//    utilisateur.setUsername(inscriptionDTO.getUsername());
//    utilisateur.setPassword(inscriptionDTO.getPassword());
//    utilisateur.setBirthdate(inscriptionDTO.getBirthdate());
//    when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

//    // Effectuez la requête POST vers /api/utilisateur/inscription
//    mockMvc.perform(post("/api/utilisateur/inscription")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(asJsonString(inscriptionDTO)))
//            .andExpect(status().isCreated())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.email").value("sara@example.com"))
//            .andExpect(jsonPath("$.username").value("sawsana"));
// }
@Test
public void testInscription_Success() throws Exception {
    // Créez un objet InscriptionDTO avec les données de test valides
    InscriptionDTO inscriptionDTO = new InscriptionDTO();
    inscriptionDTO.setEmail("hajar@test.com");
    inscriptionDTO.setUsername("hajar");
    inscriptionDTO.setPasswordValue("vGM*6$eTswFA"); // Mot de passe considéré comme fort
    inscriptionDTO.setBirthdate(LocalDate.of(1999, 9, 20)); // Date de naissance valide

    // Simulez que l'e-mail n'existe pas déjà dans la base de données
    when(utilisateurRepository.existsByEmail("hajar@test.com")).thenReturn(false);

    // Simulez la réponse de Feign Client pour la vérification de vulnérabilité du mot de passe
    PasswordCheckResponse vulnerabilityResponse = new PasswordCheckResponse(false, false, "Le mot de passe n'est pas vulnérable");
    when(passwordFeignClient.checkPasswordVulnerability(any())).thenReturn(vulnerabilityResponse);

    // Simulez la réponse de Feign Client pour la vérification de la force du mot de passe
    PasswordCheckResponse strengthResponse = new PasswordCheckResponse(true, true, "Le mot de passe est suffisamment fort");
    when(passwordFeignClient.checkPasswordStrength(any())).thenReturn(strengthResponse);

    // Simulez l'enregistrement de l'utilisateur dans la base de données
    Utilisateur utilisateur = new Utilisateur();
    utilisateur.setEmail(inscriptionDTO.getEmail());
    utilisateur.setUsername(inscriptionDTO.getUsername());
    utilisateur.setPassword(inscriptionDTO.getPassword());
    utilisateur.setBirthdate(inscriptionDTO.getBirthdate());
    when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

    // Effectuez la requête POST vers /api/utilisateur/inscription
    mockMvc.perform(post("/api/utilisateur/inscription")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(inscriptionDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").value("hajar@test.com"))
            .andExpect(jsonPath("$.username").value("hajar"));
}

private static String asJsonString(final Object obj) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper.writeValueAsString(obj);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


     @Test
    public void testInscription_EmailAlreadyExists() throws Exception {
        // Création d'un InscriptionDTO avec un e-mail existant dans la base de données
        InscriptionDTO inscriptionDTO = new InscriptionDTO();
        inscriptionDTO.setEmail("sara@example.com");

        // Simuler que l'e-mail existe déjà dans la base de données
        when(utilisateurRepository.existsByEmail("sara@example.com")).thenReturn(true);

        // Effectuer la requête POST vers /api/utilisateur/inscription et vérifier le résultat
        mockMvc.perform(post("/api/utilisateur/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(inscriptionDTO)))
                .andExpect(status().isConflict());
    }
    
    @Test
    public void testResetPassword_Success() throws Exception {
        String token = "validToken";
        String newPassword = "newPassword123@";
    
        // Simule l'appel du client feign
        doNothing().when(passwordFeignClient).resetPassword(token, newPassword);
    
        // Effectue une requête POST vers /reset avec les paramètres token et newPassword
        mockMvc.perform(post("/api/utilisateur/reset")
                .param("token", token)
                .param("newPassword", newPassword)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Vérifie que la réponse a un statut 200 OK
                .andExpect(content().string("Password has been reset."));  // Vérifie le contenu de la réponse
    
        // Vérifie que la méthode resetPassword a été appelée une fois avec les bons paramètres
        verify(passwordFeignClient, times(1)).resetPassword(token, newPassword);
        verify(logService, times(1)).logInfo("Resetting password with token: " + token);
    }

    @Test
public void testResetPassword_MissingToken() throws Exception {
    String newPassword = "newPassword123@";

    // Effectue une requête POST vers /reset sans paramètre token
    mockMvc.perform(post("/api/utilisateur/reset")
            .param("newPassword", newPassword)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());  // Vérifie que la réponse a un statut 400 Bad Request

    // Vérifie que la méthode resetPassword n'a pas été appelée
    verify(passwordFeignClient, never()).resetPassword(anyString(), anyString());
    verify(logService, never()).logInfo(anyString());
}
    @Test
    public void testInscription_FutureBirthdate() throws Exception {
        // Créez un objet InscriptionDTO avec une date de naissance future
        InscriptionDTO inscriptionDTO = new InscriptionDTO();
        inscriptionDTO.setEmail("sara@example.com");
        inscriptionDTO.setUsername("sawsana");
        inscriptionDTO.setPasswordValue("123456");
        inscriptionDTO.setBirthdate(LocalDate.now().plusDays(1)); // Date de naissance future

        // Initialisez le mockMvc pour le contrôleur
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

        // Effectuez la requête POST vers /api/utilisateur/inscription
        mockMvc.perform(post("/api/utilisateur/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"sara@example.com\", \"username\": \"sawsana\", \"passwordValue\": \"123456\", \"birthdate\": \""
                        + inscriptionDTO.getBirthdate() + "\" }"))
                .andExpect(status().isBadRequest()); // Doit retourner un statut BadRequest
    }

@Test
    public void testUpdatePassword_UserFound() throws Exception {
        String idUser = "ajc";
        String newPassword = "newPassword123@";

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(idUser);
        utilisateur.setPassword("111111");

        when(utilisateurRepository.findById(idUser)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        mockMvc.perform(put("/api/utilisateur/update")
                .param("idUser", idUser)
                .param("newPassword", newPassword)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(utilisateurRepository, times(1)).findById(idUser);
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));

        assertEquals(newPassword, utilisateur.getPassword());
    }

    @Test
    public void testRequestPasswordReset_Success() throws Exception {
        String email = "sara@example.com";

        // Simule l'appel du client feign
        doNothing().when(passwordFeignClient).requestPasswordReset(email);

        // Effectue une requête POST vers /request-reset avec le paramètre email
        mockMvc.perform(post("/api/utilisateur/request-reset")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Vérifie que la réponse a un statut 200 OK
                .andExpect(content().string("Password reset email has been sent."));  // Vérifie le contenu de la réponse

        // Vérifie que la méthode requestPasswordReset a été appelée une fois avec le bon email
        verify(passwordFeignClient, times(1)).requestPasswordReset(email);
    }

 @Test
    public void testRequestPasswordReset_MissingEmail() throws Exception {
        // Effectue une requête POST vers /request-reset sans paramètre email
        mockMvc.perform(post("/api/utilisateur/request-reset")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());  // Vérifie que la réponse a un statut 400 Bad Request

        // Vérifie que la méthode requestPasswordReset n'a pas été appelée
        verify(passwordFeignClient, never()).requestPasswordReset(anyString());
    }

    @Test
public void testResetPassword_MissingNewPassword() throws Exception {
    String token = "validToken";

    // Effectue une requête POST vers /reset sans paramètre newPassword
    mockMvc.perform(post("/api/utilisateur/reset")
            .param("token", token)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());  // Vérifie que la réponse a un statut 400 Bad Request

    // Vérifie que la méthode resetPassword n'a pas été appelée
    verify(passwordFeignClient, never()).resetPassword(anyString(), anyString());
    verify(logService, never()).logInfo(anyString());
}

@Test
public void testDeconnexion_NoSession() throws Exception {
    // Mock de l'objet HttpServletRequest sans session
    HttpServletRequest request = mock(HttpServletRequest.class);

    // Simule l'absence de session
    when(request.getSession(false)).thenReturn(null);

    // Effectue une requête POST vers /deconnexion
    mockMvc.perform(post("/api/utilisateur/deconnexion")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())  // Vérifie que la réponse a un statut 401 Unauthorized
            .andExpect(content().string("Session non trouvée."));  // Vérifie le contenu de la réponse

    // Vérifie que les logs enregistrent l'absence de session
    verify(logService, times(1)).logInfo("Logging out user");
    verify(logService, times(1)).logWarn("Session not found during logout.");
    
}


@Test
public void testGetUserByEmail_UserNotFound() throws Exception {
    // Mock de l'e-mail à rechercher
    String emailToSearch = "nonexistent@example.com";

    // Simuler le service pour retourner une valeur optionnelle vide
    when(utilisateurService.findByEmail(emailToSearch)).thenReturn(Optional.empty());

    // Initialisation du MockMvc avec le contrôleur à tester
    mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

    // Effectuer une requête GET vers /by-email avec le paramètre email
    mockMvc.perform(get("/api/utilisateur/by-email")
            .param("email", emailToSearch)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());  // Vérifie que le statut est NOT_FOUND

    // Vérifie que le service a été appelé une fois avec l'e-mail spécifié
    verify(utilisateurService, times(1)).findByEmail(emailToSearch);
}

@Test
    public void testGetUserByEmail_UserFound() throws Exception {
        // Mock de l'utilisateur et de l'e-mail à rechercher
        String emailToSearch = "test@example.com";
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("1");
        utilisateur.setEmail(emailToSearch);

        // Simuler le service pour retourner un utilisateur avec l'e-mail donné
        when(utilisateurService.findByEmail(emailToSearch)).thenReturn(Optional.of(utilisateur));

        // Initialisation du MockMvc avec le contrôleur à tester
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

        // Effectuer une requête GET vers /by-email avec le paramètre email
        mockMvc.perform(get("/api/utilisateur/by-email")
                .param("email", emailToSearch)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Vérifie que le statut est OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Vérifie que le type de contenu est JSON
                .andExpect(jsonPath("$.email").value(emailToSearch))  // Vérifie que le champ email dans la réponse JSON correspond à l'e-mail recherché
                .andExpect(jsonPath("$.id").value(utilisateur.getId()));  // Vérifie que le champ id dans la réponse JSON correspond à l'id de l'utilisateur

        // Vérifie que le service a été appelé une fois avec l'e-mail spécifié
        verify(utilisateurService, times(1)).findByEmail(emailToSearch);
    }

    @Test
    public void testUpdateUserPassword_Success() throws Exception {
        // Données de test
        String userId = "1";
        String hashedPassword = "hashedPassword";

        // Configuration du mock pour que la mise à jour réussisse
        doNothing().when(utilisateurService).updatePassword(userId, hashedPassword);

        // Initialisation du MockMvc avec le contrôleur à tester
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

        // Effectuer une requête PUT vers /{id}/password avec le corps de la requête
        mockMvc.perform(put("/api/utilisateur/{id}/password", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(hashedPassword))
                .andExpect(status().isNoContent());  // Vérifie que le statut est NO_CONTENT

        // Vérifie que le service a été appelé une fois avec les paramètres spécifiés
        verify(utilisateurService, times(1)).updatePassword(userId, hashedPassword);
        verify(logService, times(1)).logInfo("Updated password for user with ID: " + userId);
    }

    @Test
    public void testUpdateUserPassword_Failure() throws Exception {
        // Données de test
        String userId = "1";
        String hashedPassword = "hashedPassword";
        String errorMessage = "Password update failed";

        // Configuration du mock pour que la mise à jour échoue
        doThrow(new RuntimeException(errorMessage)).when(utilisateurService).updatePassword(userId, hashedPassword);

        // Initialisation du MockMvc avec le contrôleur à tester
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

        // Effectuer une requête PUT vers /{id}/password avec le corps de la requête
        mockMvc.perform(put("/api/utilisateur/{id}/password", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(hashedPassword))
                .andExpect(status().isInternalServerError());  // Vérifie que le statut est INTERNAL_SERVER_ERROR

        // Vérifie que le service a été appelé une fois avec les paramètres spécifiés
        verify(utilisateurService, times(1)).updatePassword(userId, hashedPassword);
        verify(logService, times(1)).logError("Error updating password for user with ID: " + userId + ". Error: " + errorMessage);
    }

}
