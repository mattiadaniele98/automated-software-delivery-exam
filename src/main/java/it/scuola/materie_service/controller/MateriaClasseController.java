package it.scuola.materie_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.scuola.materie_service.model.MateriaClasseDTO;
import it.scuola.materie_service.model.MateriaClasseResponseDTO;
import it.scuola.materie_service.service.MateriaClasseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per le assegnazioni materie-classi.
 */
@Tag(name = "Materie-Classe", description = "Assegnazione delle materie alle classi scolastiche")
@RestController
@RequestMapping("/api/v1/materie-classe")
public class MateriaClasseController {

    @Autowired
    private MateriaClasseService materiaClasseService;

    @Operation(summary = "Restituisce le materie assegnate a una classe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista restituita con successo"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido")
    })
    @GetMapping
    public ResponseEntity<List<MateriaClasseResponseDTO>> trovaPerClasse(                   // Gestisce le richieste GET a /api/v1/materie-classe?idClasse={idClasse} e restituisce la lista delle materie assegnate a una classe specifica tramite ID come DTO di risposta
            @RequestParam UUID idClasse) {                                                  // Prende il parametro idClasse dalla query string della richiesta
        return ResponseEntity.ok(materiaClasseService.trovaPerClasse(idClasse));
    }

    @Operation(summary = "Assegna una materia a una classe (ruolo: SEGRETERIA, ADMIN, SUPER_ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Assegnazione creata con successo"),
        @ApiResponse(responseCode = "400", description = "Dati di input non validi"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido"),
        @ApiResponse(responseCode = "403", description = "Ruolo non autorizzato (richiesto: SEGRETERIA, ADMIN, SUPER_ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Materia non trovata"),
        @ApiResponse(responseCode = "409", description = "Materia già assegnata a questa classe")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('SEGRETERIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MateriaClasseResponseDTO> assegnaMateria(
            @RequestBody @Valid MateriaClasseDTO dto) {

        MateriaClasseResponseDTO nuova = materiaClasseService.assegnaMateria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuova);
    }

    @Operation(summary = "Rimuove l'assegnazione di una materia da una classe (solo SEGRETERIA)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Assegnazione rimossa con successo"),
        @ApiResponse(responseCode = "404", description = "Assegnazione non trovata"),
        @ApiResponse(responseCode = "401", description = "Token JWT mancante o non valido"),
        @ApiResponse(responseCode = "403", description = "Ruolo non autorizzato (richiesto: SEGRETERIA)")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SEGRETERIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> rimuoviAssegnazione(@PathVariable UUID id) {
        materiaClasseService.rimuoviAssegnazione(id);
        return ResponseEntity.noContent().build();
    }
}
