package it.scuola.materie_service.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/dev")
public class DevTokenController {

    @Value("${jwt.chiave-segreta}")
    private String secret;

    @Value("${jwt.durata-ms}")
    private long durataToken;

    @Operation(
        summary = "Genera un token JWT di test",
        description = "Ruoli disponibili: STUDENTE, DOCENTE, SEGRETERIA, DIRIGENTE, ADMIN, SUPER_ADMIN"
    )
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generaToken(
            @RequestParam(defaultValue = "SEGRETERIA") String ruolo) {

        SecretKey chiave = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));

        String token = Jwts.builder()
                .subject("utente-test@scuola.it")
                .claim("ruolo", ruolo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + durataToken))
                .signWith(chiave)
                .compact();

        return ResponseEntity.ok(Map.of(
                "token", token,
                "ruolo", ruolo,
                "istruzioni", "Copia il token, clicca 'Authorize' in Swagger, incolla: Bearer <token>"
        ));
    }
}
