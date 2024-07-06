package fr.projet.api;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.projet.Request.CreatePasswordRequest;
import fr.projet.Request.PasswordCheckRequest;
import fr.projet.Response.PasswordCheckResponse;
import fr.projet.Response.PasswordGeneratedResponse;
import fr.projet.model.Password;
import fr.projet.repository.PasswordRepository;
import fr.projet.service.PasswordService;



@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
public class PasswordApiControllerTest {
@Mock
    private PasswordService passwordSrv;
 @Mock
    private PasswordRepository passwordRepository;

    @InjectMocks
    private PasswordApiController passwordController;

    private MockMvc mockMvc;


@BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordController).build();
    }

    @Test
    public void testCreatePassword_Success() throws Exception {
        CreatePasswordRequest request = new CreatePasswordRequest();
        request.setPlatformName("jeux voiture");
        request.setPasswordValue("testPassword567");
        request.setUsername("Jeremy");
        
        Password password = new Password();
        password.setIdUser("sawsan");
        password.setPasswordValue("testPassword");

        when(passwordRepository.save(any(Password.class))).thenReturn(password);

        MvcResult result = mockMvc.perform(post("/api/password/ajout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Vérifiez que la méthode save du repository a été appelée une fois avec un Password quelconque
        verify(passwordRepository, times(1)).save(any(Password.class));
    }


@Test
    public void testUpdatePassword_Success() throws Exception {
        // Define test parameters
        String idUser = "sawsan";
        String newPassword = "newPassword123";

        // Configure the mocked PasswordService to do nothing when updatePassword is called
        doNothing().when(passwordSrv).updatePassword(idUser, newPassword);

        // Simulate an HTTP PUT request to the /api/utilisateur/update endpoint
        mockMvc.perform(put("/api/password/utilisateur/update")
                .param("idUser", idUser) // Set the idUser parameter
                .param("newPassword", newPassword) // Set the newPassword parameter
                .contentType(MediaType.APPLICATION_JSON)) // Specify the content type as JSON
                .andExpect(status().isNoContent()); // Expect HTTP status 204 (No Content)

        // Verify that the PasswordService's updatePassword method was called exactly once with the specified parameters
        verify(passwordSrv, times(1)).updatePassword(idUser, newPassword);
    }


    @Test
    public void testGetPasswordByUserId_Success() throws Exception {
        // Définir les paramètres de test
        String idUser = "sawsan";
        String expectedPassword = "testPassword123";

        // Configurer le mock pour retourner le mot de passe attendu
        when(passwordSrv.getPasswordByUserId(idUser)).thenReturn(expectedPassword);

        // Simuler une requête HTTP GET vers l'endpoint
        mockMvc.perform(get("/api/password/utilisateur/" + idUser)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Vérifier que le statut HTTP est 200 (OK)
                .andExpect(content().string(expectedPassword)); // Vérifier que le contenu de la réponse est le mot de passe attendu

        // Vérifier que la méthode getPasswordByUserId du service a été appelée une fois avec le bon paramètre
        verify(passwordSrv, times(1)).getPasswordByUserId(idUser);
    }


@Test
    public void testRequestPasswordReset_Success() throws Exception {
        // Paramètres de test
        String email = "sara@example.com";

        
        doNothing().when(passwordSrv).requestPasswordReset(email);
        // Effectuer une requête POST vers /utilisateur/request-reset
        mockMvc.perform(post("/api/password/utilisateur/request-reset")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Vérifier le statut HTTP 200 (OK)
                .andExpect(content().string("Password reset email has been sent.")); // Vérifier le contenu de la réponse
    }
    @Test
    public void testResetPassword_Success() throws Exception {
        String token = "valid-token";
        String newPassword = "StrongPassword123@";

        // Mocking successful password reset
        when(passwordSrv.checkPasswordStrength(any())).thenReturn(new PasswordCheckResponse(true, false, ""));
        when(passwordSrv.checkPasswordVulnerability(any())).thenReturn(new PasswordCheckResponse(true, false, ""));
        doNothing().when(passwordSrv).resetPassword(eq(token), eq(newPassword));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/password/utilisateur/reset")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", token)
                .param("newPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset."));

        verify(passwordSrv, times(1)).resetPassword(eq(token), eq(newPassword));
    }
     @Test
    public void testResetPassword_WeakPassword() throws Exception {
        String token = "valid-token";
        String newPassword = "weak";

        // Mocking weak password scenario
        when(passwordSrv.checkPasswordStrength(any())).thenReturn(new PasswordCheckResponse(false, false, ""));
        when(passwordSrv.checkPasswordVulnerability(any())).thenReturn(new PasswordCheckResponse(false, false, ""));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/password/utilisateur/reset")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", token)
                .param("newPassword", newPassword))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Le nouveau mot de passe ne répond pas aux critères requis:\n- Le mot de passe n'est pas assez fort.\n"));

        verify(passwordSrv, never()).resetPassword(anyString(), anyString());
    }

@Test
    public void testGeneratePassword_Success() throws Exception {
        // Réponse attendue de la méthode du service
        PasswordGeneratedResponse expectedResponse = new PasswordGeneratedResponse();
        expectedResponse.setPassword("generatedPassword123");

        // Mock du service pour retourner la réponse attendue
        when(passwordSrv.generatePassword()).thenReturn(expectedResponse);

        // Configurer la requête POST vers /api/compte/generate
        MockHttpServletRequestBuilder requestBuilder = post("/api/password/compte/generate")
                .contentType(MediaType.APPLICATION_JSON);

        // Effectuer la requête POST et vérifier la réponse
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()) // Vérifier que le statut HTTP est 200 OK
                .andReturn();

        // Récupérer le contenu de la réponse JSON
        String responseJson = result.getResponse().getContentAsString();
        PasswordGeneratedResponse actualResponse = new ObjectMapper().readValue(responseJson, PasswordGeneratedResponse.class);

        // Vérifier que la réponse correspond à ce qui est attendu
        assertEquals(expectedResponse.getPassword(), actualResponse.getPassword());
    }




@Test
    public void testCheckPasswordVulnerability_PasswordIsVulnerable() throws Exception {
        // Préparation de la requête
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword("password123");

        // Mock du service pour simuler un mot de passe vulnérable
        when(passwordSrv.checkPasswordVulnerability(any(PasswordCheckRequest.class)))
                .thenReturn(new PasswordCheckResponse(false, true, "Password is vulnerable"));

        // Effectuer la requête POST vers /compte/check-vulnerability
        MvcResult result = mockMvc.perform(post("/api/password/compte/check-vulnerability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.strong").value(false))
                .andExpect(jsonPath("$.vulnerable").value(true))
                .andExpect(jsonPath("$.message").value("Password is vulnerable"))
                .andReturn();
    }


    @Test
    public void testCheckPasswordVulnerability_PasswordIsNotVulnerable() throws Exception {
        // Préparation de la requête
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword("securePassword!123");

        // Mock du service pour simuler un mot de passe non vulnérable
        when(passwordSrv.checkPasswordVulnerability(any(PasswordCheckRequest.class)))
                .thenReturn(new PasswordCheckResponse(true, false, "Password is not found in the list of stolen passwords"));

        // Effectuer la requête POST vers /compte/check-vulnerability
        MvcResult result = mockMvc.perform(post("/api/password/compte/check-vulnerability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.strong").value(true))
                .andExpect(jsonPath("$.vulnerable").value(false))
                .andExpect(jsonPath("$.message").value("Password is not found in the list of stolen passwords"))
                .andReturn();
    }





    








}


