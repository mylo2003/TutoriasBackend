package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    private Integer roleID;
    private Integer careerID;
    private String name;
    private String lastName;
    private String user;
    private String email;
    private Integer semester;
    private Double averageRating;
    private String password;
}
