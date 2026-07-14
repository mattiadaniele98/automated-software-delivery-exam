package it.scuola.materie_service.service;

import it.scuola.materie_service.model.Materia;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository per la tabella "materie".
 */
@Repository
public interface MateriaRepository extends CrudRepository<Materia, UUID> {

    boolean existsByCodice(String codice);

    boolean existsByNome(String nome);

    List<Materia> findAllByActiveTrue();
}
