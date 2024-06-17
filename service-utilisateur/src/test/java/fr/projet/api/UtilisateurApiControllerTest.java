
package fr.projet.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;



import com.fasterxml.jackson.databind.ObjectMapper;

import fr.projet.api.dto.ConnexionDTO;
import fr.projet.api.dto.InscriptionDTO;
import fr.projet.feignClient.CompteFeignClient;
import fr.projet.feignClient.NoteFeignClient;
import fr.projet.feignClient.PasswordFeignClient;
import fr.projet.model.Utilisateur;
import fr.projet.repository.UtilisateurRepository;
import fr.projet.response.NoteResponse;
import fr.projet.response.UtilisateurResponse;
import fr.projet.response.CompteResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class UtilisateurApiControllerTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private CompteFeignClient compteFeignClient;

    @Mock
    private NoteFeignClient noteFeignClient;

    @Mock
    private PasswordFeignClient passwordFeignClient;

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
        List<CompteResponse> mockComptes = new ArrayList<>();
        List<NoteResponse> mockNotes = new ArrayList<>();
       // String mockPassword = "111111";

        when(compteFeignClient.getComptesByUtilisateurId("sawsan")).thenReturn(mockComptes);
        when(noteFeignClient.getNotesByUtilisateurId("sawsan")).thenReturn(mockNotes);
      
      //  when(passwordFeignClient.getPasswordByUserId("sawsan")).thenReturn(mockPassword);

        ResponseEntity<UtilisateurResponse> responseEntity = utilisateurController.findById("sawsan");

        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok().build().getStatusCode(), responseEntity.getStatusCode());

        UtilisateurResponse utilisateurResponse = responseEntity.getBody();
        assertNotNull(utilisateurResponse);
        assertEquals(mockComptes, utilisateurResponse.getComptes());
        assertEquals(mockNotes, utilisateurResponse.getNotes());
       // assertEquals(mockPassword, utilisateurResponse.getPasswordValue());
    }

    @Test
    public void testFindById_UserNotFound() {
        when(utilisateurRepository.findById("TestUsernotFouned")).thenReturn(Optional.empty());

        ResponseEntity<UtilisateurResponse> responseEntity = utilisateurController.findById("TestUsernotFouned");

        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.notFound().build().getStatusCode(), responseEntity.getStatusCode());
    }

  @Test
    public void testConnexion_UserFound() throws Exception {
        ConnexionDTO connexionDTO = new ConnexionDTO();
        connexionDTO.setEmail("bastaoui.sawsan@gmail.com");
        connexionDTO.setPasswordValue("111111");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("sawsan");
        utilisateur.setEmail("bastaoui.sawsan@gmail.com");
        utilisateur.setPassword("111111");

        when(utilisateurRepository.findByEmailAndPasswordValue("bastaoui.sawsan@gmail.com", "111111"))
                .thenReturn(Optional.of(utilisateur));

        MvcResult result = mockMvc.perform(post("/api/utilisateur/connexion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(connexionDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        Utilisateur returnedUtilisateur = new ObjectMapper().readValue(responseJson, Utilisateur.class);

        assertNotNull(returnedUtilisateur);
        assertEquals("sawsan", returnedUtilisateur.getId());
        assertEquals("bastaoui.sawsan@gmail.com", returnedUtilisateur.getEmail());
        assertEquals("111111", returnedUtilisateur.getPassword());
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


@Test
public void testInscription_Success() throws Exception {
    // Créez un objet InscriptionDTO avec les données de test
    InscriptionDTO inscriptionDTO = new InscriptionDTO();
    inscriptionDTO.setEmail("sara@example.com");
    inscriptionDTO.setUsername("sawsana");
    inscriptionDTO.setPasswordValue("123456");

    // Vérifiez que l'e-mail n'existe pas déjà dans la base de données
    when(utilisateurRepository.existsByEmail("sara@example.com")).thenReturn(false);

    // Simulez l'enregistrement de l'utilisateur dans la base de données
    Utilisateur utilisateur = new Utilisateur();
    inscriptionDTO.setEmail("sara@example.com");
   // inscriptionDTO.setBirthdate(LocalDate.of(1998, 3 ,5));
    inscriptionDTO.setUsername("sawsana");
    inscriptionDTO.setPasswordValue("123456");

    when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);


// Initialisez le mockMvc pour le contrôleur
mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();

// Effectuez la requête POST vers /api/utilisateur/inscription
MvcResult result = mockMvc.perform(post("/api/utilisateur/inscription")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(inscriptionDTO)))
        .andExpect(status().isCreated())
        .andReturn();


}



@Test
public void testInscription_EmailAlreadyExists() throws Exception {
    // Créez un objet InscriptionDTO avec un e-mail qui existe déjà dans la base de données
    InscriptionDTO inscriptionDTO = new InscriptionDTO();
    inscriptionDTO.setEmail("sara@example.com");
    inscriptionDTO.setUsername("hajarBouamouta");
    inscriptionDTO.setPasswordValue("123456");

    // Vérifiez que l'e-mail existe déjà dans la base de données
    when(utilisateurRepository.existsByEmail("sara@example.com")).thenReturn(true);

    // Effectuez la requête POST vers /inscription
    MvcResult result = mockMvc.perform(post("/api/utilisateur/inscription")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(inscriptionDTO)))
            .andExpect(status().isConflict())  // Attend un conflit si l'e-mail existe déjà
            .andReturn();

    // Vérifiez le contenu de la réponse (optionnel, dépend de votre implémentation)
    String responseContent = result.getResponse().getContentAsString();
    assertTrue(responseContent.contains("L'e-mail existe déjà."));  // Vérifie le message d'erreur renvoyé
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


}
