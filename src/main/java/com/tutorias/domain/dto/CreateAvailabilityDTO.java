package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAvailabilityDTO {
    private int classroomId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
