package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateAvailabilityDTO;
import com.tutorias.domain.dto.ResponseAvailabilityDTO;
import com.tutorias.domain.model.Availability;
import com.tutorias.domain.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService {
    @Autowired
    private AvailabilityRepository availabilityRepository;

    public List<Availability> getAll() {
        return availabilityRepository.getAll();
    }

    public Optional<Availability> getById(int availabilityId) {
        return availabilityRepository.getById(availabilityId);
    }

    public List<Availability> filterAvailability(Integer classroomId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return availabilityRepository.filterAvailability(classroomId, dayOfWeek, startTime, endTime);
    }

    public void createAvailability(CreateAvailabilityDTO availability) {
        availabilityRepository.create(availability);
    }

    public void updateAvailability(int scheduleId, CreateAvailabilityDTO availability) {
        availabilityRepository.update(scheduleId, availability);
    }

    public void deleteAvailability(int availabilityId) {
        availabilityRepository.delete(availabilityId);
    }

    public List<ResponseAvailabilityDTO> obtenerDisponibilidades(LocalDate fecha, Integer salonId) {
        return availabilityRepository.obtenerDisponibilidades(fecha, salonId);
    }
}
