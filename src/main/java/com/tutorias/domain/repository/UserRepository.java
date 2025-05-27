package com.tutorias.domain.repository;

import com.tutorias.domain.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();
}
