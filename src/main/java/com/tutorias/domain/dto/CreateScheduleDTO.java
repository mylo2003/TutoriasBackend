package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleDTO {
    private int availabilityId;
    private int subjectId;
    private int userId;
    private String description;
    private LocalDateTime scheduleDate;
}
