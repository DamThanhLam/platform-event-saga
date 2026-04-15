package org.sento.platform.event.saga.common.event;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PlatformEventEnvelope(
    String eventId,
    String eventType,
    int eventVersion,
    Instant occurredAt,
    String sourceService,
    String aggregateType,
    String aggregateId,
    long aggregateVersion,
    String correlationId,
    String causationId,
    String sagaId,
    String traceId,
    String tenantId,
    Map<String, String> headers,
    JsonNode payload
) {
    public PlatformEventEnvelope {
        headers = headers == null ? Map.of() : Map.copyOf(headers);
    }
}
