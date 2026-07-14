package it.scuola.materie_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Filtro JWT eseguito una volta per ogni richiesta: legge l'header
 * "Authorization: Bearer <token>", valida il token con JwtService e, se valido,
 * imposta utente e ruolo nel SecurityContext. Se manca o non è valido, la richiesta
 * prosegue e verrà eventualmente bloccata dalla SecurityConfig.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {                   // Estende OncePerRequestFilter per essere eseguito una volta per ogni richiesta HTTP

    private static final Set<String> PUBLIC_PREFIXES = Set.of(              // Percorsi pubblici che non richiedono autenticazione JWT
            "/swagger-ui", "/v3/", "/dev/"
    );

    @Autowired
    private JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {         // Esclude i percorsi pubblici dal filtro JWT, ad esempio quelli per Swagger UI e OpenAPI
        String path = request.getRequestURI();
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(                                        // Metodo principale del filtro: legge l'header Authorization, valida il token JWT e, se valido, imposta l'autenticazione nel SecurityContext
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);             // Estrae il token JWT rimuovendo il prefisso "Bearer "

        if (jwtService.isTokenValid(token) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {               // Se il token JWT è valido e non c'è già un'autenticazione nel contesto di sicurezza, estrae i dati dal token e imposta l'autenticazione

            String username = jwtService.extractSubject(token);
            String role     = jwtService.extractRole(token);

            // "ROLE_" è il prefisso richiesto da Spring Security per i ruoli
            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
