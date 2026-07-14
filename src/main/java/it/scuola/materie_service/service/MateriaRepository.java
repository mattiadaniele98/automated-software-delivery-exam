package it.scuola.materie_service.service;

import it.scuola.materie_service.model.Materia;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository per la tabella "materie".
 */
@Repository
public interface MateriaRepository extends CrudRepository<Materia, UUID> {                      // Interfaccia che estende CrudRepository per fornire operazioni CRUD sulla tabella "materie" nel database db_materie

    boolean existsByCodice(String codice);                      // Metodo per verificare se esiste una materia con un determinato codice nel database

    boolean existsByNome(String nome);                          // Metodo per verificare se esiste una materia con un determinato nome nel database

    java.util.List<Materia> findAllByActiveTrue();              // Metodo per recuperare tutte le materie attive (active = true) dal database come lista di oggetti Materia
}
