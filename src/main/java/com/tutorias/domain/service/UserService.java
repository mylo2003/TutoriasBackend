package com.tutorias.domain.service;

import com.tutorias.domain.model.User;
import com.tutorias.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAll(){ return userRepository.getAll(); }

    public Optional<User> getById(Integer idUser){
        return userRepository.getById(idUser);
    }
}
