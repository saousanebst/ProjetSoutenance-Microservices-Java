package fr.projet.api;

import java.security.KeyPair;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.Repository.NoteRepository;
import fr.projet.Repository.PrivateKeyRepository;
import fr.projet.Response.NoteResponse;
import fr.projet.model.Note;
import fr.projet.model.PrivateKey;
import fr.projet.request.CreateNoteRequest;
import fr.projet.service.CryptoService;
import fr.projet.service.NoteLogService;
import fr.projet.service.NoteService;
import jakarta.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/api/note")
// @CrossOrigin("*")
public class NoteApiController {
@Autowired
NoteRepository noteRepository;

@Autowired
NoteService noteSrv;
 @Autowired
    private NoteLogService noteLogService;

@Autowired
CryptoService cryptoService;

@Autowired
PrivateKeyRepository privateKeyRepository;
//find all

//findAll
@GetMapping
    public List<NoteResponse> findAll() {
        List<Note> notes = this.noteRepository.findAll();
        List<NoteResponse> response = new ArrayList<>();

        for (Note note : notes) {
            NoteResponse noteResponse = new NoteResponse();

            BeanUtils.copyProperties(note, noteResponse);

            response.add(noteResponse);

            }
        
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable String id) {
        Optional<Note> note = noteSrv.getNoteById(id);
        if (note.isPresent()) {
            return ResponseEntity.ok(note.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //findallbyidUser

    @GetMapping("/utilisateur/{idUser}")
    public List<NoteResponse> getNotesByUtilisateurId(@PathVariable String idUser) {
        List<Note> notes = noteRepository.findAllByIdUser(idUser);
        List<NoteResponse> response = new ArrayList<>();

        for (Note note : notes) {
            NoteResponse noteResponse = new NoteResponse();
            BeanUtils.copyProperties(note, noteResponse);
            response.add(noteResponse);
        }

        return response;
    }



//update

@PutMapping("/{id}")
	public Note updateNote (@PathVariable String id,@RequestBody Note note) 
	{
		note.setId(id);
        Note updatedNote = noteSrv.update(note);
        noteLogService.logInfo("Updated note with ID: " + id);
        return updatedNote;
	}
	



@PatchMapping("/{id}")
public Note updatePartielleNote(@PathVariable String id, @RequestBody Note note) {
    try {
        // Récupérer la note existante par ID
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));

        // Mettre à jour les champs si les valeurs sont fournies dans la requête
        if (note.getNom() != null) {
            existingNote.setNom(note.getNom());
            logger.info("Updated nom of note with ID: {}", id);
        }
        if (note.getDescription() != null) {
            existingNote.setDescription(note.getDescription());
            noteLogService.logInfo("Updated description of note with ID: {}"  +id);
        }

        // Vérifier si le contenu a été modifié avant de le mettre à jour
        if (note.getContenu() != null && !note.getContenu().equals(existingNote.getContenu())) {
            // Chiffrer le nouveau contenu avec la clé publique existante de la note
            String encryptedContent = cryptoService.encryptNoteWithPublicKey(note.getContenu(), existingNote.getPublicKey());
            noteLogService.logInfo("Updated content of note with ID:" + id);
            existingNote.setContenu(encryptedContent);
        }

        // Mettre à jour la date de modification
        existingNote.setDateModif(LocalDate.now());

        // Sauvegarder la note mise à jour
        Note updatedNote = noteRepository.save(existingNote);
        noteLogService.logInfo("Partially updated note with ID: " + id);
        return updatedNote;
    } catch (Exception e) {
        // Gérer les exceptions comme EntityNotFoundException, ou d'autres erreurs
        // Retourner une réponse appropriée ou gérer l'erreur comme nécessaire
        noteLogService.logError("Failed to partially update note with ID: " + id + ". Reason: " + e.getMessage());
        throw new RuntimeException("Failed to update note with id: " + id, e);
    }
}




@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        try {
            noteSrv.deleteNoteById(id);
            noteLogService.logInfo("Deleted note with ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            noteLogService.logError("Failed to delete note with ID: " + id + ". Reason: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(NoteApiController.class);

    @PostMapping("/ajout")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@RequestBody CreateNoteRequest request) {
        try {
            // Vérifiez si l'ID utilisateur est présent dans la requête
            if (request.getIdUser() == null || request.getIdUser().isEmpty()) {
                throw new Exception("L'ID utilisateur est requis.");
            }
    
            // Générer une paire de clés RSA
            KeyPair keyPair = cryptoService.generateKeyPair();
    
            // Chiffrer le contenu de la note avec la clé publique
            String publicKeyStr = cryptoService.encodePublicKey(keyPair.getPublic());
            String encryptedContent = cryptoService.encryptNoteWithPublicKey(request.getContenu(), publicKeyStr);
    
            // Enregistrer la note avec le contenu chiffré, la clé publique et l'ID utilisateur
            Note note = new Note();
            BeanUtils.copyProperties(request, note);
            note.setContenu(encryptedContent);
            note.setPublicKey(publicKeyStr);
            note.setIdUser(request.getIdUser()); // Assurez-vous que l'ID utilisateur est attribué
            this.noteRepository.save(note);
    
            // Enregistrer la clé privée associée à la note
            PrivateKey privateKeyEntity = new PrivateKey();
            privateKeyEntity.setNoteId(note.getId());
            privateKeyEntity.setPrivateKey(cryptoService.encodePrivateKey(keyPair.getPrivate()));
            this.privateKeyRepository.save(privateKeyEntity);
            
            noteLogService.logInfo("Created note with ID: " + note.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(note.getId().toString());
        } catch (Exception e) {
            noteLogService.logError("Failed to create note. Reason: " + e.getMessage());
            logger.error("Erreur lors de la création de la note : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de la note : " + e.getMessage());
        }
    }
 

   @PostMapping("/decryptNote")
   public ResponseEntity<String> decryptNote(@RequestParam String noteId) {
       try {
           // Récupérer la note et la clé privée associée
           Optional<Note> noteOptional = noteRepository.findById(noteId);
           if (!noteOptional.isPresent()) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note non trouvée");
           }
   
           Note note = noteOptional.get();
           Optional<fr.projet.model.PrivateKey> privateKeyOptional = privateKeyRepository.findByNoteId(noteId);
           if (!privateKeyOptional.isPresent()) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Clé privée non trouvée pour cette note");
           }
   
           // Déchiffrer le contenu de la note avec la clé privée
           String encryptedContent = note.getContenu();
           String privateKeyStr = privateKeyOptional.get().getPrivateKey();
           String decryptedContent = cryptoService.decryptNoteWithPrivateKey(encryptedContent, privateKeyStr);
   
           return ResponseEntity.ok(decryptedContent);
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du déchiffrement du contenu de la note : " + e.getMessage());
       }
   }
  }