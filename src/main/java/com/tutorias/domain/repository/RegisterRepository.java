package com.tutorias.domain.repository;

import com.tutorias.domain.dto.RegisterDTO;
import com.tutorias.domain.dto.RegisterResponseDTO;

public interface RegisterRepository {
    RegisterResponseDTO registerUser(RegisterDTO registerDTO);
}
