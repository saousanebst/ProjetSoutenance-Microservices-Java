package fr.projet.api;

import java.security.KeyPair;
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
import fr.projet.service.NoteService;


@RestController
@RequestMapping("/api/note")
// @CrossOrigin("*")
public class NoteApiController {
@Autowired
NoteRepository noteRepository;

@Autowired
NoteService noteSrv;


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
		return noteSrv.update(note);
	}
	

//updatepartielle


@PatchMapping("/{id}")
	public Note updatePartielleNote (@PathVariable String id,@RequestBody Note note) 
	{
		note.setId(id);
		return noteSrv.updatePartiel(note);
	}


//delete	
	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable String id) 
	{
		noteSrv.deleteNoteById(id);
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
    
            return ResponseEntity.status(HttpStatus.CREATED).body(note.getId().toString());
        } catch (Exception e) {
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