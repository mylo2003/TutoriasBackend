package com.tutorias.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenUrlOnlyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/notificacion/conectar/") &&
                request.getHeader("Authorization") != null) {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Para esta ruta el token debe enviarse en la URL, no en headers");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
