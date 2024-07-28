package fr.projet.repository.secondary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.projet.model.secondary.PrivateKey;

@Repository
public interface PrivateKeyRepository extends JpaRepository<PrivateKey,String> {
    Optional<PrivateKey> findByCompteId( String compteId);
}
