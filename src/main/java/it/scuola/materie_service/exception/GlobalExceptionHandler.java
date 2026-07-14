package it.scuola.materie_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Gestore globale delle eccezioni: intercetta le eccezioni lanciate dai controller
 * e le trasforma in risposte HTTP con il codice e il messaggio corretti.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {               // Gestisce le eccezioni di tipo NotFound, restituendo una risposta HTTP 404 con un corpo JSON contenente informazioni sull'errore
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(                     // Crea una mappa con le informazioni sull'errore da restituire come corpo della risposta
                "timestamp", LocalDateTime.now().toString(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {           // Eccezione generica per gestire altre eccezioni che contengono uno status HTTP, restituendo il codice e il messaggio appropriati
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", ex.getStatusCode().value(),
                "error", ex.getReason() != null ? ex.getReason() : "Error",
                "message", ex.getMessage()
        ));
    }
}
