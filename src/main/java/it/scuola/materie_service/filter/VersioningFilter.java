package it.scuola.materie_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro che reindirizza internamente le richieste a /materie e /materie-classe
 * verso /api/v1/materie e /api/v1/materie-classe, in modo trasparente per il client.
 * I prefissi sono configurati in application.properties (forward.version.from/to).
 */
@Component
public class VersioningFilter extends OncePerRequestFilter {

    @Value("${forward.version.from}")
    private List<String> versionFrom;

    @Value("${forward.version.to}")
    private List<String> versionTo;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        for (int i = 0; i < versionFrom.size(); i++) {
            String from = versionFrom.get(i).trim();

            // evita che /materie faccia match anche con /materie-classe
            if (path.startsWith(from) &&
                    (path.length() == from.length() ||
                     path.charAt(from.length()) == '/' ||
                     path.charAt(from.length()) == '?')) {

                String sottoPath = path.substring(from.length());

                request.getRequestDispatcher(versionTo.get(i).trim() + sottoPath)
                       .forward(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
