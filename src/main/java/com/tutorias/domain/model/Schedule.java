package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Integer scheduleId;
    private Integer classroomId;
    private Integer subjectId;
    private Integer userId;
    private String description;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mode;
    private String type;
    private List<Booking> bookings;
}
