package fr.projet.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.Repository.NoteRepository;
import fr.projet.Response.NoteResponse;
import fr.projet.model.Note;
import fr.projet.request.CreateNoteRequest;
import fr.projet.service.NoteService;


@RestController
@RequestMapping("/api/note")
@CrossOrigin("*")
public class NoteApiController {
@Autowired
NoteRepository noteRepository;

@Autowired
NoteService noteSrv;

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



//Create

@PostMapping("/ajout")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody CreateNoteRequest request) {
        Note  note= new Note();
        
        BeanUtils.copyProperties(request, note);

        this.noteRepository.save(note);

        return note.getId();
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


































}
