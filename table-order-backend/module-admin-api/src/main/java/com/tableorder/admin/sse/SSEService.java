package com.tableorder.admin.sse;

import com.tableorder.admin.sse.dto.OrderEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SSEService {

    private static final Logger log = LoggerFactory.getLogger(SSEService.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30분

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> storeEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long storeId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        storeEmitters.computeIfAbsent(storeId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(storeId, emitter));
        emitter.onTimeout(() -> removeEmitter(storeId, emitter));
        emitter.onError(e -> removeEmitter(storeId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("connected"));
        } catch (IOException e) {
            log.warn("Failed to send initial SSE event for store {}", storeId);
            removeEmitter(storeId, emitter);
        }

        log.info("SSE subscribed for store {}, total connections: {}",
                storeId, getConnectionCount(storeId));
        return emitter;
    }

    public void publish(Long storeId, OrderEventData eventData) {
        CopyOnWriteArrayList<SseEmitter> emitters = storeEmitters.get(storeId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventData.type())
                        .data(eventData));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }

    @Scheduled(fixedRate = 15000)
    public void heartbeat() {
        storeEmitters.forEach((storeId, emitters) -> {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }
            emitters.removeAll(deadEmitters);
        });
    }

    private void removeEmitter(Long storeId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = storeEmitters.get(storeId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                storeEmitters.remove(storeId);
            }
        }
    }

    private int getConnectionCount(Long storeId) {
        CopyOnWriteArrayList<SseEmitter> emitters = storeEmitters.get(storeId);
        return emitters != null ? emitters.size() : 0;
    }
}
