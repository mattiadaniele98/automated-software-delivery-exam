package it.scuola.materie_service.model;

import java.util.UUID;

/**
 * DTO di output per la risposta relativa a un'assegnazione materia-classe.
 */
public record MateriaClasseResponseDTO(
        UUID id,
        UUID idClasse,
        MateriaResponseDTO materia,
        Integer oreSettimanaliPersonalizzate
) {
    public MateriaClasseResponseDTO(MateriaClasse mc) {
        this(
            mc.getId(),
            mc.getIdClasse(),
            new MateriaResponseDTO(mc.getMateria()),
            mc.getOreSettimanaliPersonalizzate()
        );
    }
}
