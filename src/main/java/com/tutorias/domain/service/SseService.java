package com.tutorias.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Integer, LocalDateTime> connectionTimes = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SseService.class);

    public SseEmitter subscribe(Integer userId) {
        // Cerrar conexión existente si la hay
        disconnectUser(userId);

        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);
        connectionTimes.put(userId, LocalDateTime.now());

        // Configurar callbacks
        emitter.onCompletion(() -> {
            logger.info("Conexión SSE completada para usuario: {}", userId);
            cleanupUser(userId);
        });

        emitter.onTimeout(() -> {
            logger.info("Conexión SSE expirada para usuario: {}", userId);
            cleanupUser(userId);
        });

        emitter.onError((e) -> {
            logger.error("Error en conexión SSE para usuario {}: {}", userId, e.getMessage());
            cleanupUser(userId);
        });

        // Enviar evento de conexión exitosa
        try {
            emitter.send(SseEmitter.event()
                    .name("conexion-establecida")
                    .data("Conexión SSE establecida para usuario " + userId));
            logger.info("Usuario {} conectado exitosamente vía SSE", userId);
        } catch (IOException e) {
            logger.error("Error al enviar evento de conexión para usuario {}: {}", userId, e.getMessage());
            cleanupUser(userId);
        }

        return emitter;
    }

    public boolean sendEvent(Integer userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("tutoria-recordatorio")
                        .data(message));
                logger.info("Notificación enviada a usuario {}: {}", userId, message);
                return true;
            } catch (IOException e) {
                logger.error("Error enviando notificación a usuario {}: {}", userId, e.getMessage());
                cleanupUser(userId);
                return false;
            }
        } else {
            logger.warn("No hay conexión SSE activa para usuario {}", userId);
            return false;
        }
    }

    // Método para verificar si un usuario está conectado
    public boolean isUserConnected(Integer userId) {
        return emitters.containsKey(userId);
    }

    // Método para obtener todos los usuarios conectados
    public Set<Integer> getConnectedUsers() {
        return new HashSet<>(emitters.keySet());
    }

    // Método para obtener el tiempo de conexión de un usuario
    public LocalDateTime getUserConnectionTime(Integer userId) {
        return connectionTimes.get(userId);
    }

    // Método para desconectar manualmente un usuario
    public boolean disconnectUser(Integer userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                logger.error("Error al completar emitter para usuario {}: {}", userId, e.getMessage());
            }
            cleanupUser(userId);
            return true;
        }
        return false;
    }

    // Método para enviar notificación a todos los usuarios conectados
    public void broadcastEvent(String message) {
        Set<Integer> usersToRemove = new HashSet<>();

        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("broadcast")
                        .data(message));
                logger.info("Broadcast enviado a usuario {}: {}", userId, message);
            } catch (IOException e) {
                logger.error("Error en broadcast para usuario {}: {}", userId, e.getMessage());
                usersToRemove.add(userId);
            }
        });

        // Limpiar usuarios con errores
        usersToRemove.forEach(this::cleanupUser);
    }

    // Método para obtener estadísticas de conexiones
    public Map<String, Object> getConnectionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConectados", emitters.size());
        stats.put("usuariosConectados", getConnectedUsers());

        Map<Integer, String> connectionDetails = new HashMap<>();
        connectionTimes.forEach((userId, time) -> {
            Duration duration = Duration.between(time, LocalDateTime.now());
            connectionDetails.put(userId, formatDuration(duration));
        });
        stats.put("tiemposConexion", connectionDetails);

        return stats;
    }

    // Método privado para limpiar recursos de un usuario
    private void cleanupUser(Integer userId) {
        emitters.remove(userId);
        connectionTimes.remove(userId);
        logger.info("Recursos limpiados para usuario: {}", userId);
    }

    // Método privado para formatear duración
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    // Método para heartbeat/ping
    public void sendHeartbeat() {
        Set<Integer> usersToRemove = new HashSet<>();

        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("ping"));
            } catch (IOException e) {
                logger.warn("Heartbeat falló para usuario {}, removiendo conexión", userId);
                usersToRemove.add(userId);
            }
        });

        usersToRemove.forEach(this::cleanupUser);
    }
}

