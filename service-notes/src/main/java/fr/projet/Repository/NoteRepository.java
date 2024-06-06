package fr.projet.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.Note;

public interface NoteRepository extends JpaRepository<Note, String> {
    

public List<Note> findAllByIdUser(String idUser);

}
