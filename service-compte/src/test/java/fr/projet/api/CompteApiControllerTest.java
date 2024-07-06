package fr.projet.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import fr.projet.OpenFeignClient.PasswordFeignClient;
import fr.projet.model.Compte;
import fr.projet.model.PrivateKey;
import fr.projet.repository.CompteRepository;
import fr.projet.repository.PrivateKeyRepository;
import fr.projet.request.CreateCompteRequest;
import fr.projet.request.PasswordCheckRequest;
import fr.projet.response.CompteResponse;
import fr.projet.response.PasswordCheckResponse;
import fr.projet.response.PasswordGeneratedResponse;
import fr.projet.service.CompteSrv;
import fr.projet.service.CryptographService;
import fr.projet.service.LogService;

import org.springframework.http.HttpStatus;



@ExtendWith(MockitoExtension.class)
public class CompteApiControllerTest {
    
@Mock
private CompteRepository compteRepository;
@Mock
private CompteSrv compteService;

@Mock
private PasswordFeignClient passwordFeignClient;

@Mock
private CryptographService cryptographService;


@Mock
private PrivateKeyRepository  privateKeyRepository;
 @Mock
    private LogService logService; 
@InjectMocks
    private CompteApiController compteApiController;

    private MockMvc mockMvc;


    private Compte compte;
    private CreateCompteRequest createCompteRequest;
    private PasswordCheckResponse passwordCheckResponse;
    private PasswordGeneratedResponse passwordGeneratedResponse;
    
    @BeforeEach
    void setUp() throws Exception {
        compte = new Compte();
        compte.setId("1");
        compte.setPassword("encryptedPassword");

        createCompteRequest = new CreateCompteRequest();
        createCompteRequest.setPassword("testPassword");

        passwordCheckResponse = new PasswordCheckResponse();
        passwordCheckResponse.setVulnerable(false);
        passwordCheckResponse.setStrong(true);

        passwordGeneratedResponse = new PasswordGeneratedResponse();
        passwordGeneratedResponse.setPassword("generatedStrongPassword");
    }
    @Test
    void testFindAll() {
        List<Compte> comptes = new ArrayList<>();
        comptes.add(compte);

        when(compteRepository.findAll()).thenReturn(comptes);
          doNothing().when(logService).logInfo(anyString());
        List<CompteResponse> response = compteApiController.findAll();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(compte.getId(), response.get(0).getId());
        verify(logService, times(2)).logInfo(anyString());
    }

    @Test
    void testGetComptesByUtilisateurId() {
        List<Compte> comptes = new ArrayList<>();
        comptes.add(compte);

        when(compteRepository.findAllByIdUser("1")).thenReturn(comptes);
        doNothing().when(logService).logInfo(anyString());

        List<CompteResponse> response = compteApiController.getComptesByUtilisateurId("1");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(compte.getId(), response.get(0).getId());
                verify(logService, times(2)).logInfo(anyString());

    }

    @Test
    void testFindById() {
        when(compteService.findById("1")).thenReturn(compte);

        CompteResponse response = compteApiController.findById("1");

        assertNotNull(response);
        assertEquals(compte.getId(), response.getId());
    }

    @Test
    void testCreate_Success() throws Exception {
        // Préparation des mocks pour une réponse de mot de passe non vulnérable et forte
        CreateCompteRequest request = new CreateCompteRequest();
        request.setPassword("StrongPassword123");

        PasswordCheckResponse mockVulnerabilityResponse = new PasswordCheckResponse();
        mockVulnerabilityResponse.setVulnerable(false);
        when(passwordFeignClient.checkPasswordVulnerability(any())).thenReturn(mockVulnerabilityResponse);

        PasswordCheckResponse mockStrengthResponse = new PasswordCheckResponse();
        mockStrengthResponse.setStrong(true);
        when(passwordFeignClient.checkPasswordStrength(any())).thenReturn(mockStrengthResponse);

        // Générer une paire de clés RSA simulée
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        when(cryptographService.generateKeyPair()).thenReturn(keyPair);

        // Simuler les méthodes d'encodage et de chiffrement
        when(cryptographService.encodePublicKey(any())).thenReturn("MockPublicKey");
        when(cryptographService.encryptPasswordWithPublicKey(any(), any())).thenReturn("EncryptedPassword");

        Compte savedCompte = new Compte();
        savedCompte.setId("1");
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> {
            Compte compte = invocation.getArgument(0);
            compte.setId("1");  // Simule la génération de l'ID par la base de données
            return compte;
        });

        PrivateKey privateKey = new PrivateKey();
        privateKey.setCompteId("1");
        privateKey.setPrivateKey("MockPrivateKey");
        when(cryptographService.encodePrivateKey(any())).thenReturn("MockPrivateKey");
        when(privateKeyRepository.save(any(PrivateKey.class))).thenReturn(privateKey);
        doNothing().when(logService).logInfo(anyString());

        // Appel de la méthode
        ResponseEntity<String> responseEntity = compteApiController.create(request);

        // Vérifications
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("1", responseEntity.getBody());
        verify(logService, times(2)).logInfo(anyString());

    }
    

    @Test
    void testDecryptPassword() throws Exception {
        when(compteRepository.findById("1")).thenReturn(Optional.of(compte));
        PrivateKey privateKey = new PrivateKey();
        privateKey.setCompteId(compte.getId());
        privateKey.setPrivateKey("privateKeyStr");
        when(privateKeyRepository.findByCompteId("1")).thenReturn(Optional.of(privateKey));
        when(cryptographService.decryptPassword("encryptedPassword", "privateKeyStr")).thenReturn("decryptedPassword");
        doNothing().when(logService).logInfo(anyString());

        ResponseEntity<String> response = compteApiController.decryptPassword("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("decryptedPassword", response.getBody());
        verify(logService, times(2)).logInfo(anyString());

    }



    @Test
    void testUpdateCompte() {
    String id = "1";
    Compte updatedCompteRequest = new Compte();
    updatedCompteRequest.setPassword("newEncryptedPassword");

    when(compteService.update(any(Compte.class))).thenAnswer(invocation -> {
        Compte compteToUpdate = invocation.getArgument(0);
        compteToUpdate.setId(id);
        return compteToUpdate;
    });

    Compte response = compteApiController.updateCompte(id, updatedCompteRequest);

    assertNotNull(response);
    assertEquals(id, response.getId());
    assertEquals("newEncryptedPassword", response.getPassword());

    verify(logService, times(1)).logInfo("Updated account with ID: " + id);

    }

    @Test
    void testDeleteById() {
        doNothing().when(compteService).deleteCompteById("1");
        doNothing().when(logService).logInfo(anyString());
        compteApiController.deleteById("1");

        // Verify that the delete method was called
        verify(compteService, times(1)).deleteCompteById("1");
        verify(logService, times(1)).logInfo(anyString());
    }

    @Test
    void testCheckPasswordStrength() {
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword("testPassword");

        when(passwordFeignClient.checkPasswordStrength(any(PasswordCheckRequest.class)))
                .thenReturn(passwordCheckResponse);
        doNothing().when(logService).logInfo(anyString());
        ResponseEntity<PasswordCheckResponse> response = compteApiController.checkPasswordStrength(request);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(passwordCheckResponse, response.getBody());
        verify(logService, times(2)).logInfo(anyString());
    }

    @Test
    void testCheckPasswordVulnerability() {
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword("testPassword");

        when(passwordFeignClient.checkPasswordVulnerability(any(PasswordCheckRequest.class)))
                .thenReturn(passwordCheckResponse);
        doNothing().when(logService).logInfo(anyString());
        ResponseEntity<PasswordCheckResponse> response = compteApiController.checkPasswordVulnerability(request);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(passwordCheckResponse, response.getBody());
        verify(logService, times(2)).logInfo(anyString());

    }

    @Test
    void testGeneratePassword() {
        PasswordGeneratedResponse passwordGeneratedResponse = new PasswordGeneratedResponse();
        passwordGeneratedResponse.setPassword("generatedPassword");

        when(passwordFeignClient.generatePassword()).thenReturn(passwordGeneratedResponse);
        doNothing().when(logService).logInfo(anyString());

        ResponseEntity<PasswordGeneratedResponse> response = compteApiController.generatePassword();

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(passwordGeneratedResponse, response.getBody());
        verify(logService, times(2)).logInfo(anyString());

    }




}