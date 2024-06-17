package fr.projet.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.model.PrivateKey;

public interface PrivateKeyRepository extends JpaRepository<PrivateKey, String> {
    Optional<PrivateKey> findByNoteId(String noteId);
}