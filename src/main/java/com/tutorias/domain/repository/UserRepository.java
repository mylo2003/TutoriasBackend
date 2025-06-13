package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateUserDTO;
import com.tutorias.domain.dto.EditSubjectUserDTO;
import com.tutorias.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();
    Optional<User> getById(Integer idUser);
    void updateAverage(int tutorId, double average);
    void updateUser(int idUser, CreateUserDTO body, String encryptedPassword);
    List<Integer> getSubjectsByIdUser(Integer idUser);
    void deleteUserSubjects(EditSubjectUserDTO body);
    void deleteUser(Integer idUser);
    boolean existsByIdUser(Integer idUser);
    boolean existsByUsernameAndNotId(String username, Integer idUser);
    boolean existsByEmailAndNotId(String email, Integer idUser);
    List<User> findProfesoresByMateriaIds(List<Integer> subjectIds, Integer rolId);
}
