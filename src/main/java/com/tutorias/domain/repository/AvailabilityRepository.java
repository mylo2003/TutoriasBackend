package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateAvailabilityDTO;
import com.tutorias.domain.model.Availability;
import org.springframework.data.domain.Page;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository {
    List<Availability> getAll();
    Optional<Availability> getById(int availabilityId);
    Page<Availability> filterAvailability(Integer classroomId, String dayOfWeek, LocalTime startTime, LocalTime endTime, int page, int elements);
    void create(CreateAvailabilityDTO availabilityId);
    void delete(int availabilityId);
}
