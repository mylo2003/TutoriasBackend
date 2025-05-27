package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private int scheduleId;
    private int classroomId;
    private int subjectId;
    private int userId;
    private String description;
    private LocalDateTime scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mode;
    private List<Booking> bookings;
}
