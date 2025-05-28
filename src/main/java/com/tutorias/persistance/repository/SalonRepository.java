package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateClassroomDTO;
import com.tutorias.domain.model.Classroom;
import com.tutorias.domain.repository.ClassroomRepository;
import com.tutorias.persistance.crud.BloqueCrudRepository;
import com.tutorias.persistance.crud.SalonCrudRepository;
import com.tutorias.persistance.entity.Bloque;
import com.tutorias.persistance.entity.Salon;
import com.tutorias.persistance.mapper.ClassroomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SalonRepository implements ClassroomRepository {
    @Autowired
    private SalonCrudRepository jpaRepository;
    @Autowired
    private BloqueCrudRepository bloqueCrudRepository;
    @Autowired
    private ClassroomMapper mapper;

    @Override
    public List<Classroom> getAll() {
        List<Salon> salones = jpaRepository.findAll();
        return mapper.toClassrooms(salones);
    }

    @Override
    public Optional<Classroom> getById(int classroomId) {
        return jpaRepository.findById(classroomId)
                .map(mapper::toClassroom);
    }

    @Override
    public void create(CreateClassroomDTO classroom) {
        Bloque bloque = bloqueCrudRepository.findById(classroom.getBlockId())
                .orElseThrow(() -> new RuntimeException("Bloque no encontrado"));

        Salon salon = new Salon();

        salon.setBloque(bloque);
        salon.setDescripcion(classroom.getDescription());
        salon.setUbicacion(classroom.getLocation());
        jpaRepository.save(salon);
    }

    @Override
    public void delete(int classroomId) {
        Salon salon = jpaRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Salón no encontrado"));

        jpaRepository.deleteById(salon.getIdSalon());
    }
}
