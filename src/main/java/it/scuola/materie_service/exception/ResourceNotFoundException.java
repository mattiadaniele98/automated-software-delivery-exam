package it.scuola.materie_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione lanciata quando una risorsa non viene trovata nel DB.
 * Spring Boot la traduce automaticamente in una risposta HTTP 404.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String messaggio) {
        super(messaggio);
    }
}
