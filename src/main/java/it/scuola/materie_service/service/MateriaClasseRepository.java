package it.scuola.materie_service.service;

import it.scuola.materie_service.model.MateriaClasse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

/**
 * Repository per la tabella "materie_classe".
 */
@Repository
public interface MateriaClasseRepository extends CrudRepository<MateriaClasse, UUID> {

    Collection<MateriaClasse> findByIdClasse(UUID idClasse);

    boolean existsByIdClasseAndMateriaId(UUID idClasse, UUID materiaId);

    // usato per decidere hard delete o soft delete su una materia
    boolean existsByMateriaId(UUID materiaId);
}
