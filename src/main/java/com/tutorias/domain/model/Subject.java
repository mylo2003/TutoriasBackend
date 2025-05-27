package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    private int subjectId;
//    private int careerId;
    private String subjectName;
//    private Boolean isDeleted;
    private Career career;
    private List<SubjectUser> subjectUsers;
}
