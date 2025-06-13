package com.tutorias.domain.service;

import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserSecurityService implements UserDetailsService {

    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioCrudRepository.findByUsuarioWithRol(username)
                .orElseThrow(() -> new UsernameNotFoundException("User" + username + "Not found"));

        String roleName = "ROLE_" + usuario.getRol().getNombreRol().toUpperCase();

        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getContrasenia())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(roleName)))
                .build();
    }

}
