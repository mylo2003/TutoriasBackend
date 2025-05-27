package com.tutorias.persistance.repository;

import com.tutorias.domain.model.User;
import com.tutorias.domain.repository.UserRepository;
import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.entity.Usuario;
import com.tutorias.persistance.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioRepository implements UserRepository{

    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;

    @Autowired
    private UserMapper mapper;

    public List<User> getAll(){ return mapper.toUsers((List<Usuario>) usuarioCrudRepository.findAll()); }
}
