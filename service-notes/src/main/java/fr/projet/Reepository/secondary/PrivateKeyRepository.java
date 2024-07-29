package fr.projet.repository.secondary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.secondary.PrivateKey;

public interface PrivateKeyRepository extends JpaRepository<PrivateKey, String> {
    Optional<PrivateKey> findByNoteId(String noteId);
}