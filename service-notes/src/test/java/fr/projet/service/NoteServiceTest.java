package fr.projet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.projet.Repository.NoteRepository;
import fr.projet.model.Note;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

@Test
    public void testUpdateNote_Success() throws Exception {
       
 // Données de test
        Note originalNote = new Note("123", "NameNota1", "Original Description",LocalDate.now().minusDays(1), "contenue à voir","sawsan");
        Note updatedNote = new Note("123", "exellente", "top original note ",LocalDate.now(), "contenue chercher ","sawsan");

        // Mock du comportement de findById pour retourner la note originale
        when(noteRepository.findById(eq(originalNote.getId()))).thenReturn(Optional.of(originalNote));

        // Mock du comportement de save pour retourner la note mise à jour
        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

        // Appel de la méthode à tester
        Note returnedNote = noteService.update(updatedNote);

        // Vérification du résultat
        assertEquals(updatedNote.getNom(), returnedNote.getNom());
        assertEquals(updatedNote.getDescription(), returnedNote.getDescription());
        assertEquals(updatedNote.getContenu(), returnedNote.getContenu());
        assertEquals(updatedNote.getDateModif(), returnedNote.getDateModif());
         // Vérification que save a été appelé une fois avec la note mise à jour
    verify(noteRepository, times(1)).save(any(Note.class));

}

@Test
public void testUpdateNote_NotFound() {
    // Données de test
    Note updatedNote = new Note("123", "Updated Name", "Updated Description", LocalDate.now(), "updated content", "sawsan");

    // Mock du comportement de findById pour retourner Optional.empty()
    when(noteRepository.findById(eq(updatedNote.getId()))).thenReturn(Optional.empty());

    // Appel de la méthode à tester et vérification de l'exception lancée
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        noteService.update(updatedNote);
    });

    // Vérification du message d'erreur
    assertEquals("Note not found with id " + updatedNote.getId(), exception.getMessage());

    // Vérification que save n'a pas été appelé
    verify(noteRepository, never()).save(any(Note.class));
}



@Test
    public void testDeleteNoteById() {
        // Données de test
        String id = "123sana";

        // Appel de la méthode à tester
        noteService.deleteNoteById(id);

        verify(noteRepository, times(1)).deleteById(eq(id));
    }



    @Test
    public void testUpdatePartiel_NoteNotFound() {
        // Données de test pour le scénario où la note n'est pas trouvée
        Note noteWithoutId = new Note(); // Note sans id

        // Appel de la méthode à tester et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            noteService.updatePartiel(noteWithoutId);
        });

        // Vérification du message d'erreur
        assertEquals("Un update sans id erreur?!", exception.getMessage());
    }


    @Test
    public void testUpdatePartiel_Success() {
        // Données de test
        String id = "123";
        Note existingNote = new Note(id, "Original Name", "Original Description", LocalDate.now(), "Original Content", "sawsan");
        Note updatedNote = new Note(id, "Updated Name", null, null, null, null); // Note avec les champs à mettre à jour

        // Mock du comportement de findById pour retourner la note existante
        when(noteRepository.findById(eq(id))).thenReturn(Optional.of(existingNote));

        // Appel de la méthode à tester
        Note resultNote = noteService.updatePartiel(updatedNote);

        // Vérification que findById a été appelé avec l'id spécifié
        verify(noteRepository).findById(eq(id));

        // Vérification que save a été appelé avec la note mise à jour
        verify(noteRepository).save(eq(existingNote));

       
    }












    
  }























