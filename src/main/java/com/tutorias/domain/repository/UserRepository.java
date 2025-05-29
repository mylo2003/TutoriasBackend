package com.tutorias.domain.repository;

import com.tutorias.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();
    Optional<User> getById(Integer idUser);
}
