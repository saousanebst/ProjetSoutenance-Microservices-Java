package fr.projet.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;

import fr.projet.Repository.NoteRepository;
import fr.projet.model.Note;

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
            updatedNote.setDateModif(null);
            updatedNote.setDateAjout(null);
            return noteRepository.save(updatedNote);
        } else {
            throw new RuntimeException("Note not found with id " + note.getId());
        }
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
	



}
