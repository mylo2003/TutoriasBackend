package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateCareerDTO;
import com.tutorias.domain.model.Career;
import com.tutorias.domain.repository.CareerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareerService {
    @Autowired
    private CareerRepository careerRepository;

    public List<Career> getAll() {
        return careerRepository.getAll();
    }

    public Optional<Career> getById(int careerId) {
        return careerRepository.getById(careerId);
    }

    public void createCareer(CreateCareerDTO career) {
        if (career.getCareerName() == null || career.getCode() == null){
            throw new RuntimeException("No se admiten valores vacios");
        }
        careerRepository.create(career);
    }

    public void deleteCareer(int careerId) {
        careerRepository.delete(careerId);
    }
}
