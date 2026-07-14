package it.scuola.materie_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Servizio per la validazione e il parsing dei token JWT emessi da utenti-service.
 * Verifica firma e scadenza del token ed espone i dati del payload (subject e ruolo).
 */
@Service
public class JwtService {

    @Value("${jwt.chiave-segreta}")                     // La chiave segreta per firmare i token JWT, letta da application.properties e decodificata da Base64
    private String secret;

    private SecretKey getSigningKey() {                 // La chiave in application.properties è una stringa Base64. Prima la decodifica in bytes, poi la converte in una SecretKey usabile da JJWT. È privato perché serve solo internamente agli altri metodi.
        byte[] keyBytes = Base64.getDecoder().decode(secret);       
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token) {                 // Verifica se il token JWT è valido (firma corretta e non scaduto) restituendo true o false
        try {
            Jwts.parser()                                       // Crea un parser JWT configurato con la chiave di firma e tenta di validare e parsare il token. Se la firma è errata o il token è scaduto, viene lanciata un'eccezione.
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractSubject(String token) {                    // Estrae il subject (ad esempio l'email dell'utente) dal token JWT, restituendo null se il token non è valido
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {                   // Estrae il ruolo (ad esempio "STUDENTE", "DOCENTE", ecc.) dal token JWT, restituendo null se il token non è valido
        return getClaims(token).get("ruolo", String.class);
    }

    private Claims getClaims(String token) {                        // Estrae i claims (payload) dal token JWT, restituendo null se il token non è valido
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
