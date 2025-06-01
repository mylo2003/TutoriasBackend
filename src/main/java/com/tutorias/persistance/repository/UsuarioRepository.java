package com.tutorias.persistance.repository;

import com.tutorias.domain.model.User;
import com.tutorias.domain.repository.UserRepository;
import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.entity.Usuario;
import com.tutorias.persistance.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository implements UserRepository{
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;
    @Autowired
    private UserMapper mapper;

    @Override
    public List<User> getAll(){ return mapper.toUsers(usuarioCrudRepository.findAll()); }

    @Override
    public Optional<User> getById(Integer idUser){
        return usuarioCrudRepository.findById(idUser).map(mapper::toUser);
    }

    @Override
    public void updateAverage(int tutorId, double average) {
        Usuario tutor = usuarioCrudRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        tutor.setValoracionPromedio(average);
    }
}
