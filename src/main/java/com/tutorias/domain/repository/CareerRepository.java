package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateCareerDTO;
import com.tutorias.domain.model.Career;

import java.util.List;
import java.util.Optional;

public interface CareerRepository {
    List<Career> getAll();
    Optional<Career> getById(int careerId);
    void create(CreateCareerDTO career);
    void delete(int careerId);
}
