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
public interface MateriaClasseRepository extends CrudRepository<MateriaClasse, UUID> {              // Interfaccia che estende CrudRepository per fornire operazioni CRUD sulla tabella "materie_classe" nel database db_materie

    Collection<MateriaClasse> findByIdClasse(UUID idClasse);                                        // Metodo per recuperare tutte le assegnazioni di materie per una classe specifica dal database come collezione di oggetti MateriaClasse

    boolean existsByIdClasseAndMateriaId(UUID idClasse, UUID materiaId);                            // Metodo per verificare se esiste già un'assegnazione di una materia a una classe specifica nel database

    // usato per decidere hard delete o soft delete su una materia
    boolean existsByMateriaId(UUID materiaId);
}
