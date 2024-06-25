package fr.projet.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;

import fr.projet.Repository.NoteRepository;
import fr.projet.model.Note;
import fr.projet.request.CreateNoteRequest;

@Service
public class NoteService {
    @Autowired 
    NoteRepository noteRepository;

//update id
 
     public Note update(Note note) {
        Optional<Note> Note = noteRepository.findById(note.getId());
        if (Note.isPresent()) {
            Note updatedNote = Note.get();
            updatedNote.setNom(note.getNom());
            updatedNote.setDescription(note.getDescription());
            updatedNote.setDateModif(note.getDateModif());
            updatedNote.setDateAjout(note.getDateAjout());
			updatedNote.setContenu(note.getContenu());
            return noteRepository.save(updatedNote);
        } else {
            throw new RuntimeException("Note not found with id " + note.getId());
        }
    }

	public Optional<Note> getNoteById(String id) {
        return noteRepository.findById(id);
    }

//delete

public void deleteNoteById (String id){
    this.noteRepository.deleteById(id);
}



//modif partielle
@PatchMapping("/{id}")
public Note updatePartiel(Note noteJSON) 
	{
		if(noteJSON.getId()==null) 
		{
			throw new RuntimeException("Un update sans id erreur?!");
		}
		Optional<Note> opt = noteRepository.findById(noteJSON.getId());
		if(opt.isPresent()) {
			Note noteBdd = (Note) opt.get();
		
			if(noteJSON.getNom()!=null) 
			{
				noteBdd.setNom(noteJSON.getNom());
			}
			if(noteJSON.getDescription()!=null) 
			{
				noteBdd.setDescription(noteJSON.getDescription());
			}
			if(noteJSON.getDateAjout()!=null) 
			{
				noteBdd.setDateAjout(noteJSON.getDateAjout());
			}
			if(noteJSON.getDateModif()!=null) 
			{
				noteBdd.setDateModif(noteJSON.getDateModif());
			}
			if(noteJSON.getContenu()!=null) 
			{
				noteBdd.setContenu(noteJSON.getContenu());
			}
			return noteRepository.save(noteBdd);
		}
		else 
		{
			return null;
		}
		
		
	}
	
public String create(CreateNoteRequest request) {
        // Générer une note à partir de la requête
        Note note = new Note();
        BeanUtils.copyProperties(request, note);

        // Enregistrer la note dans le repository
        Note savedNote = noteRepository.save(note);

        // Retourner l'ID de la note créée
        return savedNote.getId();
    }



	


}
