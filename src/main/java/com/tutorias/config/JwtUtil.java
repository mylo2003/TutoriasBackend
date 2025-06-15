package com.tutorias.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private final Algorithm ALGORITHM;

    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;

    public JwtUtil() {
        String secretKey = "mi_llave_secreta_de_prueba";
        this.ALGORITHM = Algorithm.HMAC256(secretKey);
    }

    public String create(String username) {
        Usuario usuario = usuarioCrudRepository.findByUsuarioWithRol(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        if (usuario.getRol() == null) {
            throw new RuntimeException("Usuario sin rol asignado: " + username);
        }
        String role = usuario.getRol().getNombreRol().toLowerCase();
        System.out.println("Ingreso el usuario "+usuario.getNombre()+" con el rol "+role);
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withClaim("userId", usuario.getIdUsuario())
                .withIssuer("sistema-tutorias")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12)))
                .sign(ALGORITHM);
    }

    public boolean isValid(String jwt) {
        try {
            JWT.require(ALGORITHM)
                    .build()
                    .verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            System.err.println("Token inválido: " + e.getMessage());
            return false;
        }
    }

    public String getUsername(String jwt) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(jwt)
                .getSubject();
    }

    public Integer getUserId(String jwt) {
        try {
            return JWT.require(ALGORITHM)
                    .build()
                    .verify(jwt)
                    .getClaim("userId")
                    .asInt();
        } catch (NullPointerException | JWTVerificationException e) {
            throw new RuntimeException("Error al obtener userId del token", e);
        }
    }
}