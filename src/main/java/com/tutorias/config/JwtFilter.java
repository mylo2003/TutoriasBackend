package com.tutorias.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Validar Header Authorizacion
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Obtener el token JWT
            String jwt = authHeader.substring(7); // "Bearer " tiene 7 caracteres

            // 4. Validar el token
            if (jwtUtil.isValid(jwt)) {
                // 5. Obtener el username/cedula
                String username = jwtUtil.getUsername(jwt);

                // 6. Cargar el usuario
                UserDetails user = userDetailsService.loadUserByUsername(username);

                // 7. Autenticar
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                ((UserDetails) user).getUsername(),
                                null, // No necesitamos la contraseña aquí
                                user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Log the error but don't throw it
            System.err.println("Error processing JWT token: " + e.getMessage());
        }

        // 8. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);

    }
}
