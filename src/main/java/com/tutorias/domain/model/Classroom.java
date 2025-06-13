package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {
    private int classroomId;
    private int blockId;
    private String description;
    private String location;
}
