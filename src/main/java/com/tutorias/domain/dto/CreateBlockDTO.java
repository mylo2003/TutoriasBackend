package com.tutorias.domain.dto;

import com.tutorias.domain.model.Classroom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlockDTO {
    private String blockName;
    private String section;
    private List<Classroom> classrooms;
}
