package com.tutorias.web.controller;

import com.tutorias.domain.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public class NotificationController {

    @Autowired
    private SseService sseService;

    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable Integer userId) {
        return sseService.subscribe(userId);
    }
}

