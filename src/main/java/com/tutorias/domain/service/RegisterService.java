package com.tutorias.domain.service;

import com.tutorias.domain.dto.RegisterDTO;
import com.tutorias.domain.dto.RegisterResponseDTO;
import com.tutorias.domain.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    @Autowired
    private RegisterRepository repository;

    public RegisterResponseDTO registerUser(RegisterDTO registerDTO){
        return repository.registerUser(registerDTO);
    }
}
