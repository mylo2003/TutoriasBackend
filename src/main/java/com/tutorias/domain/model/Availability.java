package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availability {
    private Integer availabilityId;
    private Integer classroomId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
