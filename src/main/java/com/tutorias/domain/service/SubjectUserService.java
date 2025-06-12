package com.tutorias.domain.service;

import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.domain.repository.SubjectUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectUserService {
    @Autowired
    private SubjectUserRepository subjectUserRepository;

    public List<SubjectFilter> filterByUserId(Integer userId) {
        return subjectUserRepository.filterByUserId(userId);
    }
}
