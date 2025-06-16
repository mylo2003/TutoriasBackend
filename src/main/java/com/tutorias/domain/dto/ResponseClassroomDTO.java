package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClassroomDTO {
    private int classroomId;
    private String description;
    private String location;
    private String blockName;
    private String section;
    private int capacity;
}
