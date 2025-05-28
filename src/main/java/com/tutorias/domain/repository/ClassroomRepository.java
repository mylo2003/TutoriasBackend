package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateClassroomDTO;
import com.tutorias.domain.model.Classroom;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository {
    List<Classroom> getAll();
    Optional<Classroom> getById(int classroomId);
    void create(CreateClassroomDTO classroom);
    void delete(int classroomId);
}
