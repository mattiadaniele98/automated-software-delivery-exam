package it.scuola.materie_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.scuola.materie_service.model.MateriaDTO;
import it.scuola.materie_service.model.MateriaResponseDTO;
import it.scuola.materie_service.model.MateriaUpdateDTO;
import it.scuola.materie_service.service.MateriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per il catalogo delle materie.
 * Le rotte sono sotto /api/v1/materie; il VersioningFilter fa arrivare qui
 * anche le chiamate a /materie.
 */
@Tag(name = "Materie", description = "Catalogo delle materie scolastiche")
@RestController
@RequestMapping("/api/v1/materie")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @Operation(summary = "Restituisce il catalogo completo delle materie")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista materie restituita con successo"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido")
    })
    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> trovaTutte() {
        return ResponseEntity.ok(materiaService.trovaTutte());
    }

    @Operation(summary = "Restituisce il dettaglio di una materia tramite ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Materia trovata"),
        @ApiResponse(responseCode = "404", description = "Materia non trovata"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> trovaPerID(
            @PathVariable UUID id) {
        return ResponseEntity.ok(materiaService.trovaPerID(id));
    }

    @Operation(summary = "Crea una nuova materia (ruolo: SEGRETERIA, ADMIN, SUPER_ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Materia creata con successo"),
        @ApiResponse(responseCode = "400", description = "Dati di input non validi"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido"),
        @ApiResponse(responseCode = "403", description = "Ruolo non autorizzato (richiesto: SEGRETERIA)"),
        @ApiResponse(responseCode = "409", description = "Materia già esistente con stesso codice o nome")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('SEGRETERIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MateriaResponseDTO> creaMateria(
            @RequestBody @Valid MateriaDTO dto) {

        MateriaResponseDTO nuova = materiaService.creaMateria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuova);
    }

    @Operation(summary = "Aggiorna una materia esistente (ruolo: SEGRETERIA, ADMIN, SUPER_ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Materia aggiornata con successo"),
        @ApiResponse(responseCode = "404", description = "Materia non trovata"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido"),
        @ApiResponse(responseCode = "403", description = "Ruolo non autorizzato (richiesto: SEGRETERIA)")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SEGRETERIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MateriaResponseDTO> aggiornaMateria(
            @PathVariable UUID id,
            @RequestBody MateriaUpdateDTO dto) {

        return ResponseEntity.ok(materiaService.aggiornaMateria(id, dto));
    }

    @Operation(summary = "Elimina una materia (ruolo: SEGRETERIA, ADMIN, SUPER_ADMIN)",
               description = "Hard delete se non ha riferimenti, soft delete (active=false) se ha assegnazioni")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Materia eliminata o disattivata"),
        @ApiResponse(responseCode = "404", description = "Materia non trovata"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido"),
        @ApiResponse(responseCode = "403", description = "Ruolo non autorizzato (richiesto: SEGRETERIA)")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SEGRETERIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> eliminaMateria(@PathVariable UUID id) {
        materiaService.eliminaMateria(id);
        return ResponseEntity.noContent().build();
    }
}
