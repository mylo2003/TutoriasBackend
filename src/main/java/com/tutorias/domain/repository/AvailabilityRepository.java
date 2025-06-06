package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateAvailabilityDTO;
import com.tutorias.domain.model.Availability;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository {
    List<Availability> getAll();
    Optional<Availability> getById(int availabilityId);
    List<Availability> filterAvailability(int classroomId, String dayOfWeek, LocalTime startTime, LocalTime endTime);
    void create(CreateAvailabilityDTO availabilityId);
    void update(int availabilityId, CreateAvailabilityDTO availability);
    void delete(int availabilityId);
    void updateOccupied(int availabilityId);
}
