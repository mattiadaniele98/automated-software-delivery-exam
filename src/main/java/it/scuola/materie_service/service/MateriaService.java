package it.scuola.materie_service.service;

import it.scuola.materie_service.exception.ResourceNotFoundException;
import it.scuola.materie_service.model.Materia;
import it.scuola.materie_service.model.MateriaDTO;
import it.scuola.materie_service.model.MateriaResponseDTO;
import it.scuola.materie_service.model.MateriaUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service                        // Indica a Spring che questa classe è un "service" e può essere iniettata come dipendenza
public class MateriaService {

    @Autowired                                  // Inietta automaticamente il repository per accedere al database
    private MateriaRepository materiaRepository;

@Autowired
    private MateriaClasseRepository materiaClasseRepository;

    public MateriaResponseDTO creaMateria(MateriaDTO dto) {                                         // Crea una nuova materia nel database a partire dai dati ricevuti nel DTO
        if (this.materiaRepository.existsByCodice(dto.codice())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Esiste già una materia con codice: " + dto.codice());
        }
        if (this.materiaRepository.existsByNome(dto.nome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Esiste già una materia con nome: " + dto.nome());
        }

        Materia salvata = this.materiaRepository.save(new Materia(dto));                            // Hibernate fa INSERT e salva la nuova materia nel database e restituisce il DTO di risposta

        return new MateriaResponseDTO(salvata);
    }

    public List<MateriaResponseDTO> trovaTutte() {                           // Restituisce tutte le materie attive nel database come lista di DTO di risposta
        return this.materiaRepository.findAllByActiveTrue()
                .stream()                                   // Apre una lista ed elabora gli elementi uno per uno
                .map(MateriaResponseDTO::new)               // Per ogni materia, crea un nuovo DTO di risposta usando il costruttore che accetta una Materia
                .toList();                                  // Raccoglie tutti i DTO in una lista e la restituisce
    }

    public MateriaResponseDTO trovaPerID(UUID id) {                         // Restituisce una materia attiva dal database a partire dall'ID, o lancia un'eccezione se non trovata
        Materia materia = this.materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Materia non trovata con id: " + id));
        return new MateriaResponseDTO(materia);
    }

    public MateriaResponseDTO aggiornaMateria(UUID id, MateriaUpdateDTO dto) {          // Aggiorna una materia esistente nel database a partire dall'ID e dai dati ricevuti nel DTO di aggiornamento restituisce 404 se non trovata
        Materia materia = this.materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Materia non trovata con id: " + id));

        // Aggiorna solo i campi non nulli (partial update)
        if (dto.nome() != null) {
            materia.setNome(dto.nome());
        }
        if (dto.descrizione() != null) {
            materia.setDescrizione(dto.descrizione());
        }
        if (dto.oreSettimanali() != null) {
            materia.setOreSettimanali(dto.oreSettimanali());
        }
        if (dto.tipoMateria() != null) {
            materia.setTipoMateria(dto.tipoMateria());
        }
        if (dto.active() != null) {
            materia.setActive(dto.active());
        }

        return new MateriaResponseDTO(this.materiaRepository.save(materia));
    }

    public void eliminaMateria(UUID id) {                               // Elimina una materia dal database a partire dall'ID. Se la materia ha riferimenti in materie_classe, viene eseguita una soft delete (active = false), altrimenti viene eliminata fisicamente.
        Materia materia = this.materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Materia non trovata con id: " + id));

        if (this.materiaClasseRepository.existsByMateriaId(id)) {       // Se la materia ha riferimenti in materie_classe, esegue una soft delete impostando active = false e salvando la materia aggiornata nel database
            // ha riferimenti in materie_classe: soft delete
            materia.setActive(false);
            this.materiaRepository.save(materia);
        } else {
            this.materiaRepository.deleteById(id);                      // Altrimenti, elimina fisicamente la materia dal database
        }
    }
}
