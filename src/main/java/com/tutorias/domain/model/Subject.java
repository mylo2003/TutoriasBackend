package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    private int subjectId;
    private int careerId;
    private String subjectName;
    private int code;
    private int credits;
    private Career career;
}
