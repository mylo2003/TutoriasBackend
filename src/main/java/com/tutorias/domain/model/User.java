package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userId;
    private String name;
    private String lastName;
    private String username;
    private String email;
    private int semester;
    private Double averageRating;
    private Integer totalSchedule;
    private Career career;
    private Role role;
    private List<SubjectUser> subjectUsers;
}
