package it.scuola.materie_service.service;

import it.scuola.materie_service.exception.ResourceNotFoundException;
import it.scuola.materie_service.model.Materia;
import it.scuola.materie_service.model.MateriaClasse;
import it.scuola.materie_service.model.MateriaClasseDTO;
import it.scuola.materie_service.model.MateriaClasseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class MateriaClasseService {

    @Autowired
    private MateriaClasseRepository materiaClasseRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    public MateriaClasseResponseDTO assegnaMateria(MateriaClasseDTO dto) {
        Materia materia = this.materiaRepository.findById(dto.idMateria())              // Recupera la materia dal database usando l'idMateria fornito nel DTO, o restituisce un'eccezione se non trovata 404
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Materia non trovata con id: " + dto.idMateria()));

        if (this.materiaClasseRepository.existsByIdClasseAndMateriaId(dto.idClasse(), dto.idMateria())) {           // Controlla se esiste già un'assegnazione della materia alla classe, e restituisce un'eccezione 409 se sì
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Questa materia è già assegnata alla classe");
        }

        MateriaClasse mc = new MateriaClasse();                         // Crea una nuova entità MateriaClasse per rappresentare l'assegnazione della materia alla classe
        mc.setIdClasse(dto.idClasse());
        mc.setMateria(materia);
        mc.setOreSettimanaliPersonalizzate(dto.oreSettimanaliPersonalizzate());

        return new MateriaClasseResponseDTO(this.materiaClasseRepository.save(mc));
    }

    public List<MateriaClasseResponseDTO> trovaPerClasse(UUID idClasse) {                                       // Restituisce tutte le assegnazioni di materie per una classe specifica come lista di DTO di risposta
        Collection<MateriaClasse> lista = this.materiaClasseRepository.findByIdClasse(idClasse);
        return lista.stream()
                .map(MateriaClasseResponseDTO::new)
                .toList();
    }

    public void rimuoviAssegnazione(UUID id) {                              // Rimuove un'assegnazione di materia a una classe dal database a partire dall'ID dell'assegnazione, o restituisce un'eccezione 404 se non trovata
        if (!this.materiaClasseRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Assegnazione non trovata con id: " + id);
        }
        this.materiaClasseRepository.deleteById(id);
    }
}
