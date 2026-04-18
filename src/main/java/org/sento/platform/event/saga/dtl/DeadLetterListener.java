package org.sento.platform.event.saga.dtl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sento.platform.event.saga.common.event.EventEnvelope;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterListener {

    private final ObjectMapper objectMapper;

    @Value("${platform.event.dlts}")
    private String dltTopic;

    @PostConstruct
    public void init() {
        log.info("DLT topic resolved = {}", dltTopic);
    }

    @KafkaListener(topics = "${platform.event.dlts}", groupId = "${platform.event.group.dlt}")
    public void consumeDlt(String message) throws Exception {
        try {
            EventEnvelope event = objectMapper.readValue(message, EventEnvelope.class);
            MDC.put("correlationId", event.getCorrelationId());
            if (event.getCausationId() != null) MDC.put("causationId", event.getCausationId());
            if (event.getSagaId() != null) MDC.put("sagaId", event.getSagaId());
            if (event.getTraceId() != null) MDC.put("traceId", event.getTraceId());
            log.error("Received DLT event: type={} id={} payload={}", event.getEventType(), event.getEventId(), event.getPayload());
        } finally {
            MDC.clear();
        }
    }
}
