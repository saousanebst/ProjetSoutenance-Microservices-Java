package fr.projet.Repository.primary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.primary.Note;

public interface NoteRepository extends JpaRepository<Note, String> {
    

public List<Note> findAllByIdUser(String idUser);

}
