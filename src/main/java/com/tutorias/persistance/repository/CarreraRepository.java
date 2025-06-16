package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateCareerDTO;
import com.tutorias.domain.model.Career;
import com.tutorias.domain.repository.CareerRepository;
import com.tutorias.persistance.crud.CarreraCrudRepository;
import com.tutorias.persistance.entity.Carrera;
import com.tutorias.persistance.mapper.CareerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarreraRepository implements CareerRepository {
    @Autowired
    private CarreraCrudRepository jpaRepository;
    @Autowired
    private CareerMapper mapper;

    @Override
    public List<Career> getAll() {
        List<Carrera> carreras = jpaRepository.findAll();
        return mapper.toCareers(carreras);
    }

    @Override
    public Optional<Career> getById(int careerId) {
        return jpaRepository.findById(careerId)
                .map(mapper::toCareer);
    }

    @Override
    public void create(CreateCareerDTO career) {
        boolean yaExiste = jpaRepository.findByNombreCarrera(career.getCareerName()).isPresent();
        boolean codigoExistente = jpaRepository.findByCodigo(career.getCode()).isPresent();

        if (yaExiste) {
            throw new RuntimeException("La carrera ya se encuentra registrada");
        } else if(codigoExistente){
            throw new RuntimeException("El codigo ya se encuentra registrado en otro programa");
        }

        Carrera carrera = new Carrera();

        carrera.setNombreCarrera(career.getCareerName());
        carrera.setCodigo(career.getCode());
        jpaRepository.save(carrera);
    }

    @Override
    public void delete(int careerId) {
        Carrera carrera = jpaRepository.findById(careerId)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));

        jpaRepository.deleteById(carrera.getIdCarrera());
    }
}
