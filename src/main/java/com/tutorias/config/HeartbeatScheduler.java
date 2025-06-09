package com.tutorias.config;

import com.tutorias.domain.service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableScheduling
public class HeartbeatScheduler {
    @Autowired
    private SseService sseService;

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatScheduler.class);

    // Enviar heartbeat cada 30 segundos para mantener conexiones activas
    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {
        logger.debug("Enviando heartbeat a todas las conexiones SSE");
        sseService.sendHeartbeat();
    }

    // Log de estadísticas cada 5 minutos
    @Scheduled(fixedRate = 300000)
    public void logConnectionStats() {
        Map<String, Object> stats = sseService.getConnectionStats();
        logger.info("Estadísticas SSE: {}", stats);
    }
}
