package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateClassroomDTO;
import com.tutorias.domain.model.Classroom;
import com.tutorias.domain.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository classroomRepository;

    public List<Classroom> getAll() {
        return classroomRepository.getAll();
    }

    public Optional<Classroom> getById(int classroomId) {
        return classroomRepository.getById(classroomId);
    }

    public void createClassroom(CreateClassroomDTO classroom) {
        classroomRepository.create(classroom);
    }
}
