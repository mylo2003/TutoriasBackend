package com.tutorias.web.controller;

import com.tutorias.domain.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/notificacion")
public class NotificationController {

    @Autowired
    private SseService sseService;

    @GetMapping("/conectar/{userId}")
    public SseEmitter subscribe(@PathVariable Integer userId) {
        return sseService.subscribe(userId);
    }

    // Nuevo endpoint para verificar si un usuario está conectado
    @GetMapping("/conectado/{userId}")
    public ResponseEntity<Map<String, Object>> isUserConnected(@PathVariable Integer userId) {
        boolean isConnected = sseService.isUserConnected(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("conectado", isConnected);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // Endpoint para obtener todos los usuarios conectados
    @GetMapping("/conectados")
    public ResponseEntity<Map<String, Object>> getConnectedUsers() {
        Set<Integer> connectedUsers = sseService.getConnectedUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("usuariosConectados", connectedUsers);
        response.put("totalConectados", connectedUsers.size());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // Endpoint para desconectar manualmente un usuario
    @DeleteMapping("/desconectar/{userId}")
    public ResponseEntity<Map<String, String>> disconnectUser(@PathVariable Integer userId) {
        boolean wasConnected = sseService.disconnectUser(userId);
        Map<String, String> response = new HashMap<>();

        if (wasConnected) {
            response.put("mensaje", "Usuario " + userId + " desconectado exitosamente");
            response.put("estado", "success");
        } else {
            response.put("mensaje", "Usuario " + userId + " no estaba conectado");
            response.put("estado", "info");
        }

        return ResponseEntity.ok(response);
    }

    // Endpoint para enviar notificación a un usuario específico
    @PostMapping("/enviar/{userId}")
    public ResponseEntity<Map<String, String>> sendNotification(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> notification) {

        String message = notification.get("mensaje");
        boolean sent = sseService.sendEvent(userId, message);

        Map<String, String> response = new HashMap<>();
        if (sent) {
            response.put("mensaje", "Notificación enviada exitosamente");
            response.put("estado", "success");
        } else {
            response.put("mensaje", "Usuario no conectado, notificación no enviada");
            response.put("estado", "error");
        }

        return ResponseEntity.ok(response);
    }
}

