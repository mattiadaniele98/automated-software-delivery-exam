package it.scuola.materie_service.config;

import it.scuola.materie_service.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configurazione di Spring Security: filtro JWT, gestione stateless
 * delle sessioni e risposte di errore per 401/403.
 */
@Configuration              // Indica a Spring che questa classe contiene la configurazione e i bean per la sicurezza
@EnableWebSecurity          // Abilita la sicurezza web di Spring, attivando il supporto per la configurazione basata su Java (anziché XML)
@EnableMethodSecurity       // Abilita la sicurezza a livello di metodo, permettendo di usare annotazioni come @PreAuthorize sui controller per specificare i ruoli autorizzati
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Bypassa completamente Spring Security per Swagger e path pubblici.
     * Questi path non passano nemmeno per il filtro JWT.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {                  // Configura Spring Security per ignorare completamente le richieste a determinati percorsi, ad esempio quelli per la documentazione Swagger e l'endpoint di health check, in modo che non richiedano autenticazione JWT e non vengano filtrati da JwtAuthFilter
        return web -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/**", "/dev/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {                // Configura la catena di filtri di sicurezza di Spring Security, disabilitando CSRF, impostando la gestione delle sessioni su stateless, richiedendo autenticazione per tutte le richieste, personalizzando le risposte per 401 e 403 e aggiungendo il filtro JWT prima del filtro di autenticazione standard
        http
            .csrf(csrf -> csrf.disable())                   // Disabilita la protezione CSRF, ovvero una protezione per le sessioni con cookie, che non è necessaria per un'API REST stateless che utilizza JWT
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write(
                                "{\"status\":401,\"error\":\"Non autorizzato\",\"message\":\"Token JWT mancante o non valido\"}"
                        );
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write(
                                "{\"status\":403,\"error\":\"Accesso negato\",\"message\":\"Ruolo non autorizzato per questa operazione\"}"
                        );
                    })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
