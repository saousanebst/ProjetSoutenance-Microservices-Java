package fr.projet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.projet.model.Compte;
import fr.projet.model.PrivateKey;
import fr.projet.repository.CompteRepository;
import fr.projet.repository.PrivateKeyRepository;

public class CompteServiceTest {
    

    @Mock
    private CompteRepository compteRepository;

    @InjectMocks
    private CompteSrv compteSrv;


    @Mock
    private PrivateKeyRepository privateKeyRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testUpdate() {
        // Mock data
        Compte existingCompte = new Compte();
        existingCompte.setId("1");
        existingCompte.setPlatformName("Old Platform");

        Compte updatedCompte = new Compte();
        updatedCompte.setId("1");
        updatedCompte.setPlatformName("New Platform");

        // Mock repository behavior
        when(compteRepository.findById("1")).thenReturn(Optional.of(existingCompte));
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // Perform update
        Compte result = compteSrv.update(updatedCompte);

        // Verify repository interactions
        when(compteRepository.findById("1")).thenReturn(Optional.of(existingCompte));
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Assertions
        assertEquals("New Platform", result.getPlatformName());
        assertNotNull(result.getUpdateDate());
        assertEquals(LocalDate.now(), result.getUpdateDate());
    }


@Test
void testDeleteCompteById() {
    String idToDelete = "1";

    // Perform delete
    compteSrv.deleteCompteById(idToDelete);

    // Verify repository interaction
    verify(compteRepository, times(1)).deleteById(idToDelete);
}

@Test
void testFindById() {
    String idToFind = "1";
    Compte foundCompte = new Compte();
    foundCompte.setId(idToFind);

    // Mock repository behavior
    when(compteRepository.findById(idToFind)).thenReturn(Optional.of(foundCompte));

    // Perform find
    Compte result = compteSrv.findById(idToFind);

    // Verify repository interaction
    verify(compteRepository, times(1)).findById(idToFind);

    // Assertions
    assertEquals(idToFind, result.getId());
}

@Test
void testFindByCompteId() {
    String compteIdToFind = "1";
    PrivateKey foundPrivateKey = new PrivateKey();
    foundPrivateKey.setCompteId(compteIdToFind);

    // Mock repository behavior
    when(privateKeyRepository.findByCompteId(compteIdToFind)).thenReturn(Optional.of(foundPrivateKey));

    // Perform find
    PrivateKey result = compteSrv.findByCompteId(compteIdToFind);

    // Verify repository interaction
    verify(privateKeyRepository, times(1)).findByCompteId(compteIdToFind);

    // Assertions
    assertEquals(compteIdToFind, result.getCompteId());
}

}