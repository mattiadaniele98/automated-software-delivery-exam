package it.scuola.materie_service.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Controller disponibile solo nel profilo "sviluppo": genera token JWT di test
 * per usare Swagger senza dover avviare utenti-service, che normalmente li emette.
 */
@Profile("sviluppo")
@Tag(name = "DEV - Token Generator",
     description = "⚠️ Solo sviluppo. Genera token JWT per testare le API con Swagger.")
@RestController
@RequestMapping("/dev")                                                 // Tutte le rotte di questo controller saranno sotto /dev, ad esempio /dev/token per generare un token JWT di test dev escluso dalla sicurezza
public class DevTokenController {

    @Value("${jwt.chiave-segreta}")                                     // La chiave segreta per firmare i token JWT, letta da application.properties e decodificata da Base64
    private String secret;
                                                            // Stesso meccanismo di @Autowired ma per valori semplici invece che oggetti.
    @Value("${jwt.durata-ms}")                                          // La durata dei token JWT in millisecondi, letta da application.properties
    private long durataToken;

    @Operation(
        summary = "Genera un token JWT di test",
        description = "Ruoli disponibili: STUDENTE, DOCENTE, SEGRETERIA, DIRIGENTE, ADMIN, SUPER_ADMIN"
    )
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generaToken(
            @RequestParam(defaultValue = "SEGRETERIA") String ruolo) {                  // Gestisce le richieste POST a /dev/token e genera un token JWT di test con il ruolo specificato come parametro (default: SEGRETERIA)

        SecretKey chiave = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));      // Crea una chiave segreta a partire dalla stringa decodificata da Base64, da usare per firmare il token JWT

        String token = Jwts.builder()                                                   // Decodifica la chiave per firmare
                .subject("utente-test@scuola.it")
                .claim("ruolo", ruolo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + durataToken))
                .signWith(chiave)
                .compact();

        return ResponseEntity.ok(Map.of(                                                 // Restituisce il token JWT generato e map.of crea una mappa al volo senza bisogno di un DTO dedicato
                "token", token,
                "ruolo", ruolo,
                "istruzioni", "Copia il token, clicca 'Authorize' in Swagger, incolla: Bearer <token>"
        ));
    }
}
