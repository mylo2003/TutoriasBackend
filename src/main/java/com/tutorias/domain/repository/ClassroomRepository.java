package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateClassroomDTO;
import com.tutorias.domain.model.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository {
    List<Classroom> getAll();
    Optional<Classroom> getById(int classroomId);
    Page<Classroom> getByBlockId(Integer blockId, Pageable pageable);
    void create(CreateClassroomDTO classroom);
    void delete(int classroomId);
}
