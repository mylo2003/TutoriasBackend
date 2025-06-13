package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseScheduleEditDTO {
    private Integer scheduleId;
    private Integer blockId;
    private Integer availabilityId;
    private Integer classroomId;
    private Integer subjectId;
    private Integer userId;
    private String description;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mode;
    private String type;
}
