package fr.projet.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



import fr.projet.Repository.NoteRepository;
import fr.projet.Repository.PrivateKeyRepository;
import fr.projet.model.Note;
import fr.projet.model.PrivateKey;
import fr.projet.request.CreateNoteRequest;
import fr.projet.service.CryptoService;
import fr.projet.service.NoteLogService;
import fr.projet.service.NoteService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NoteApiControllerTest {
    

 @Autowired
    private MockMvc mockMvc;

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private NoteService noteSrv;
    @Autowired
    private ObjectMapper objectMapper;
    @InjectMocks
    private NoteApiController noteController;
    @Mock
    private PrivateKeyRepository privateKeyRepository;
    @Mock
    private NoteLogService noteLogService;
    @Mock
    private CryptoService cryptoService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();
    }

    @Test
    public void testFindAll() throws Exception {
        // Données de test
        Note note1 = new Note( "id1","Note1", "Description1", LocalDate.now().minusDays(2), LocalDate.now(),"Content1");
        
        Note note2 = new Note( "id2","Note2", "Description2", LocalDate.now().minusDays(1), LocalDate.now(),"Content2");

        // Mock du comportement du repository
        when(noteRepository.findAll()).thenReturn(Arrays.asList(note1, note2));

        // Appel de la méthode findAll et vérification du résultat
        mockMvc.perform(get("/api/note")
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].nom").value("Note1"))
               .andExpect(jsonPath("$[0].description").value("Description1"))
               .andExpect(jsonPath("$[0].contenu").value("Content1"))
               .andExpect(jsonPath("$[1].nom").value("Note2"))
               .andExpect(jsonPath("$[1].description").value("Description2"))
               .andExpect(jsonPath("$[1].contenu").value("Content2"));
    }


  @Test
    public void testGetNotesByUtilisateurId() throws Exception {
        // Données de test
        String idUser = "user123";
        Note note1 = new Note("123", "Note1", "Description1", LocalDate.now().minusDays(1), "Content1", "user123");
        Note note2 = new Note("124", "Note2", "Description2", LocalDate.now(), "Content2", "user123");

        // Mock du comportement du repository
        when(noteRepository.findAllByIdUser(eq(idUser))).thenReturn(Arrays.asList(note1, note2));

        // Appel de la méthode findAllByUtilisateurId et vérification du résultat
        mockMvc.perform(get("/api/note/utilisateur/{idUser}", idUser)
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value("123"))
               .andExpect(jsonPath("$[0].nom").value("Note1"))
               .andExpect(jsonPath("$[0].description").value("Description1"))
               .andExpect(jsonPath("$[0].contenu").value("Content1"))
               .andExpect(jsonPath("$[1].id").value("124"))
               .andExpect(jsonPath("$[1].nom").value("Note2"))
               .andExpect(jsonPath("$[1].description").value("Description2"))
               .andExpect(jsonPath("$[1].contenu").value("Content2"));
    }



     @Test
    public void testUpdateNote_Success() throws Exception {
        // Données de test
        String id = "123";
        Note originalNote = new Note("123", "Original Name", "Original Description", LocalDate.now().minusDays(1), "Original Content", "user123");
        Note updatedNote = new Note("123", "Updated Name", "Updated Description", LocalDate.now(), "Updated Content", "user123");

        // Mock du comportement du service
        when(noteSrv.update(any(Note.class))).thenReturn(updatedNote);

        // Convertir la note en JSON
        String updatedNoteJson = objectMapper.writeValueAsString(updatedNote);

        // Appel de la méthode updateNote et vérification du résultat
        mockMvc.perform(put("/api/note/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedNoteJson))
               .andExpect(status().isOk());

        // Vérification que la méthode update du service est appelée une fois avec la note spécifiée
        verify(noteSrv, times(1)).update(any(Note.class));
        verify(noteLogService, times(1)).logInfo("Updated note with ID: " + id);
    }



   @Test
    public void testDeleteNote_Success() throws Exception {
        // Données de test
        String id = "123";

        // Mock du comportement du service
        doNothing().when(noteSrv).deleteNoteById(id);

        // Appel de la méthode deleteById et vérification du résultat
        mockMvc.perform(delete("/api/note/{id}", id))
                .andExpect(status().isNoContent());

        // Vérification que la méthode deleteNoteById du service est appelée une fois avec l'id spécifié
        verify(noteSrv, times(1)).deleteNoteById(id);

        // Vérification que noteLogService.logInfo est appelé avec les bons arguments après la suppression réussie
        verify(noteLogService, times(1)).logInfo("Deleted note with ID: " + id);
    }





//     @Test
//     public void testCreateNote_Success() throws Exception {
//         // Données de test
//         CreateNoteRequest request = new CreateNoteRequest();
//         request.setIdUser("user123");
//         request.setNom("Test Note");
//         request.setDescription("Description of Test Note");
//         request.setContenu("Content of Test Note");
//         request.setDateAjout(LocalDate.of(2024, 7, 2)); // Utilisation de LocalDate
    
//         // Mock du comportement de cryptoService.generateKeyPair()
//         KeyPair keyPair = generateMockKeyPair();
//         when(cryptoService.generateKeyPair()).thenReturn(keyPair);
    
//         // Mock du comportement de cryptoService.encodePublicKey()
//         String publicKeyStr = "mockedPublicKey";
//         when(cryptoService.encodePublicKey(keyPair.getPublic())).thenReturn(publicKeyStr);
    
//         // Mock du comportement de cryptoService.encryptNoteWithPublicKey()
//         String encryptedContent = "mockedEncryptedContent";
//         when(cryptoService.encryptNoteWithPublicKey(request.getContenu(), publicKeyStr)).thenReturn(encryptedContent);
    
//         // Mock du comportement de noteRepository.save()
//         Note savedNote = new Note();
//         savedNote.setId("123"); // Mocked ID
//         when(noteRepository.save(any(Note.class))).thenReturn(savedNote);
    
//         // Mock du comportement de privateKeyRepository.save()
//         when(privateKeyRepository.save(any(PrivateKey.class))).thenReturn(new PrivateKey());
    
//         // Convertir la requête en JSON
//         String requestJson = objectMapper.writeValueAsString(request);
    
//         try {
//     MvcResult result = mockMvc.perform(post("/api/note/ajout")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(requestJson))
//             .andExpect(status().isCreated())
//             .andReturn();
    
//     System.out.println("Response: " + result.getResponse().getContentAsString());
// } catch (Exception e) {
//     e.printStackTrace();
//     throw e;
// }
    
//         // Vérification que noteRepository.save() est appelé une fois avec la note appropriée
//         verify(noteRepository, times(1)).save(any(Note.class));
    
//         // Vérification que privateKeyRepository.save() est appelé une fois
//         verify(privateKeyRepository, times(1)).save(any(PrivateKey.class));
    
//         // Vérification que noteLogService.logInfo est appelé une fois avec les bons arguments
//         verify(noteLogService, times(1)).logInfo("Created note with ID: " + savedNote.getId());
//     }




@Test
    public void testDecryptNote_NoteNotFound() throws Exception {
        // Données de test
        String noteId = "note123";

        // Mock du comportement du repository pour retourner une note non trouvée
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        // Initialisation du MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();

        // Appel de l'API avec MockMvc
        mockMvc.perform(post("/api/note/decryptNote")
                .contentType(MediaType.APPLICATION_JSON)
                .param("noteId", noteId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note non trouvée"));

        // Vérification de l'appel au repository
        verify(noteRepository, times(1)).findById(noteId);
        verify(privateKeyRepository, never()).findByNoteId(anyString());
        verify(cryptoService, never()).decryptNoteWithPrivateKey(anyString(), anyString());
    }



    @Test
    public void testDecryptNote_Success() throws Exception {
        // Données de test
        String noteId = "note123";
        String encryptedContent = "encryptedContent";
        String privateKeyStr = "privateKeyStr";
        String decryptedContent = "decryptedContent";

        // Mock du comportement des repositories et du service de chiffrement
        Note note = new Note();
        note.setId(noteId);
        note.setContenu(encryptedContent);

        PrivateKey privateKey = new PrivateKey();
        privateKey.setNoteId(noteId);
        privateKey.setPrivateKey(privateKeyStr);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(privateKeyRepository.findByNoteId(noteId)).thenReturn(Optional.of(privateKey));
        when(cryptoService.decryptNoteWithPrivateKey(encryptedContent, privateKeyStr)).thenReturn(decryptedContent);

        // Initialisation du MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();

        // Appel de l'API avec MockMvc
        mockMvc.perform(post("/api/note/decryptNote")
                .contentType(MediaType.APPLICATION_JSON)
                .param("noteId", noteId))
                .andExpect(status().isOk())
                .andExpect(content().string(decryptedContent));

        // Vérification des appels aux repositories et au service de chiffrement
        verify(noteRepository, times(1)).findById(noteId);
        verify(privateKeyRepository, times(1)).findByNoteId(noteId);
        verify(cryptoService, times(1)).decryptNoteWithPrivateKey(encryptedContent, privateKeyStr);
    }

    @Test
    public void testDecryptNote_PrivateKeyNotFound() throws Exception {
        // Données de test
        String noteId = "note123";
        String encryptedContent = "encryptedContent";

        // Mock du comportement du repository pour retourner une clé privée non trouvée
        Note note = new Note();
        note.setId(noteId);
        note.setContenu(encryptedContent);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(privateKeyRepository.findByNoteId(noteId)).thenReturn(Optional.empty());

        // Initialisation du MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();

        // Appel de l'API avec MockMvc
        mockMvc.perform(post("/api/note/decryptNote")
                .contentType(MediaType.APPLICATION_JSON)
                .param("noteId", noteId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Clé privée non trouvée pour cette note"));

        // Vérification des appels aux repositories
        verify(noteRepository, times(1)).findById(noteId);
        verify(privateKeyRepository, times(1)).findByNoteId(noteId);
        verify(cryptoService, never()).decryptNoteWithPrivateKey(anyString(), anyString());
    }
    private KeyPair generateMockKeyPair() {
        // Implémentation fictive pour les tests
        // Vous pouvez ajuster cela selon vos besoins réels de test
        // Par exemple, utiliser KeyPairGenerator.getInstance("RSA").generateKeyPair() dans un contexte réel
        return new KeyPair(null, null);
    }


}