package org.sento.platform.event.saga.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.sento.platform.event.saga.config.EventSagaProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventEnvelopeFactory {

    private final ObjectMapper objectMapper;
    private final CorrelationContext correlationContext;
    private final EventSagaProperties eventSagaProperties;

    public <T> EventEnvelope create(
        String eventType,
        int eventVersion,
        String aggregateType,
        String aggregateId,
        long aggregateVersion,
        String causationId,
        T payload
    ) {
        return create(
            eventType,
            eventVersion,
            aggregateType,
            aggregateId,
            aggregateVersion,
            causationId,
            Map.of(),
            payload
        );
    }

    public <T> EventEnvelope create(
        String eventType,
        int eventVersion,
        String aggregateType,
        String aggregateId,
        long aggregateVersion,
        String causationId,
        Map<String, String> headers,
        T payload
    ) {

        JsonNode payloadNode = objectMapper.valueToTree(payload);

        return EventEnvelope.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(eventType)
            .eventVersion(eventVersion)
            .occurredAt(Instant.now())
            .sourceService(eventSagaProperties.getSourceService())

            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .aggregateVersion(aggregateVersion)

            .correlationId(correlationContext.correlationId())
            .causationId(causationId)
            .sagaId(correlationContext.sagaId())
            .traceId(correlationContext.traceId())
            .tenantId(correlationContext.tenantId())

            .headers(safeHeaders(headers))
            .payload(payloadNode)
            .build();
    }

    private Map<String, String> safeHeaders(Map<String, String> headers) {
        return headers == null ? Map.of() : Map.copyOf(headers);
    }
}