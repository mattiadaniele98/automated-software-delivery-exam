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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per il catalogo delle materie.
 * Le rotte sono sotto /api/v1/materie; il VersioningFilter fa arrivare qui
 * anche le chiamate a /materie.
 */
@Tag(name = "Materie", description = "Catalogo delle materie scolastiche")              // Tag per la documentazione OpenAPI/Swagger
@RestController                                                                         // Indica a Spring che questa classe è un controller REST e può gestire richieste HTTP
@RequestMapping("/api/v1/materie")                                                      // Mappa tutte le richieste che iniziano con /api/v1/materie a questo controller
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @Operation(summary = "Restituisce il catalogo completo delle materie")              // Descrizione dell'operazione per la documentazione OpenAPI/Swagger
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista materie restituita con successo"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido")
    })
    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> trovaTutte() {                      // Gestisce le richieste GET a /api/v1/materie e restituisce la lista completa delle materie come DTO di risposta
        return ResponseEntity.ok(materiaService.trovaTutte());
    }

    @Operation(summary = "Restituisce il dettaglio di una materia tramite ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Materia trovata"),
        @ApiResponse(responseCode = "404", description = "Materia non trovata"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido")
    })
    @GetMapping("/{id}")                                                                // Get con parametro ID nella rotta, ad esempio /api/v1/materie/123e4567-e89b-12d3-a456-426614174000 e restituisce il dettaglio di quella materia specifica
    public ResponseEntity<MateriaResponseDTO> trovaPerID(
            @PathVariable UUID id) {                                                    // Gestisce le richieste GET a /api/v1/materie/{id} e restituisce il dettaglio di una materia specifica tramite ID come DTO di risposta
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
    @PreAuthorize("hasAnyRole('SEGRETERIA', 'ADMIN', 'SUPER_ADMIN')")                   // Richiede che l'utente abbia uno dei ruoli specificati per poter accedere a questa rotta
    public ResponseEntity<MateriaResponseDTO> creaMateria(
            @RequestBody @Valid MateriaDTO dto) {                                       // Prende il json della richiesta e lo mappa in un oggetto MateriaDTO, validandolo automaticamente

        MateriaResponseDTO nuova = materiaService.creaMateria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuova);                   // Restituisce lo status 201 Created e il DTO della nuova materia creata
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
            @PathVariable UUID id,                                                      // Prende l'ID della materia dalla rotta
            @RequestBody MateriaUpdateDTO dto) {                                        // Prende il json della richiesta e lo mappa in un oggetto MateriaUpdateDTO, senza validazione automatica perchè tutti i campi sono opzionali (partial update)

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
