package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateAvailabilityDTO;
import com.tutorias.domain.model.Availability;
import com.tutorias.domain.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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

    public Page<Availability> filterAvailability(Integer classroomId, String dayOfWeek, LocalTime startTime, LocalTime endTime, int page, int elements) {
        return availabilityRepository.filterAvailability(classroomId, dayOfWeek, startTime, endTime, page, elements);
    }

    public void createAvailability(CreateAvailabilityDTO availability) {
        availabilityRepository.create(availability);
    }
}
