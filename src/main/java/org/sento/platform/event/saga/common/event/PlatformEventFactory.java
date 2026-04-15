package org.sento.platform.event.saga.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlatformEventFactory {

    private final ObjectMapper objectMapper;
    private final CorrelationContext correlationContext;
    private final PlatformProperties platformProperties;

    public <T> PlatformEventEnvelope create(
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

    public <T> PlatformEventEnvelope create(
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
        return new PlatformEventEnvelope(
            UUID.randomUUID().toString(),
            eventType,
            eventVersion,
            Instant.now(),
            platformProperties.getSourceService(),
            aggregateType,
            aggregateId,
            aggregateVersion,
            correlationContext.correlationId(),
            causationId,
            correlationContext.sagaId(),
            correlationContext.traceId(),
            correlationContext.tenantId(),
            headers,
            payloadNode
        );
    }
}
