package com.tutorias.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Integer userId) {
        SseEmitter emitter = new SseEmitter(0L); // Sin timeout
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        return emitter;
    }

    public void sendEvent(Integer userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("tutoria-recordatorio")
                        .data(message));
                System.out.println("Notificación enviada a usuario " + userId + ": " + message);
            } catch (IOException e) {
                emitters.remove(userId);
                System.out.println("Error enviando notificación a usuario " + userId + ", eliminando emitter.");
            }
        } else {
            System.out.println("No hay conexión SSE activa para usuario " + userId);
        }
    }
}

