package org.sento.platform.event.saga.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope {

    private String eventId;
    private String eventType;
    private int eventVersion;
    private Instant occurredAt;
    private String sourceService;
    private String aggregateType;
    private String aggregateId;
    private long aggregateVersion;
    private String correlationId;
    private String causationId;
    private String sagaId;
    private String traceId;
    private String tenantId;
    private Map<String, String> headers;
    private JsonNode payload;

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return headers;
    }
}