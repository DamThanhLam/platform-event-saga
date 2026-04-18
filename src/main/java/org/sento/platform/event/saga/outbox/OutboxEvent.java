package org.sento.platform.event.saga.outbox;

import org.sento.platform.event.saga.common.event.EventEnvelope;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
@Getter
@Setter
public class OutboxEvent {

    private String id;
    private String source;

    private String aggregateType;
    private String aggregateId;
    private long aggregateVersion;

    private String eventType;
    private int eventVersion;

    private String topic;
    private String messageKey;

    private String correlationId;
    private String causationId;
    private String sagaId;
    private String traceId;
    private String tenantId;

    private Map<String, String> headers;

    private byte[] payload;

    private OutboxStatus status;
    private int attempts;
    private String lastError;

    private Instant createdAt;
    private Instant publishedAt;

    protected OutboxEvent() {}

    public static OutboxEvent newEvent(
        String sourceService,
        EventEnvelope event,
        String topic,
        String messageKey,
        Map<String, String> extraHeaders
    ) {
        OutboxEvent entity = new OutboxEvent();

        entity.id = event.getEventId();
        entity.aggregateType = event.getAggregateType();
        entity.aggregateId = event.getAggregateId();
        entity.aggregateVersion = event.getAggregateVersion();
        entity.eventType = event.getEventType();
        entity.eventVersion = event.getEventVersion();

        entity.source = sourceService;
        entity.topic = topic;
        entity.messageKey = messageKey;

        entity.correlationId = event.getCorrelationId();
        entity.causationId = event.getCausationId();
        entity.sagaId = event.getSagaId();
        entity.traceId = event.getTraceId();
        entity.tenantId = event.getTenantId();

        entity.headers = new LinkedHashMap<>();
        if (event.getHeaders() != null) {
            entity.headers.putAll(event.getHeaders());
        }
        if (extraHeaders != null) {
            entity.headers.putAll(extraHeaders);
        }

        entity.payload = event.getPayload();
        entity.status = OutboxStatus.NEW;
        entity.attempts = 0;
        entity.createdAt = event.getOccurredAt();

        return entity;
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.attempts++;
        this.lastError = error;
    }

    public EventEnvelope toEnvelope() {
        return EventEnvelope.builder()
            .eventId(this.id)
            .eventType(this.eventType)
            .eventVersion(this.eventVersion)
            .occurredAt(this.createdAt)
            .sourceService(this.source)

            .aggregateType(this.aggregateType)
            .aggregateId(this.aggregateId)
            .aggregateVersion(this.aggregateVersion)

            .correlationId(this.correlationId)
            .causationId(this.causationId)
            .sagaId(this.sagaId)
            .traceId(this.traceId)
            .tenantId(this.tenantId)

            .headers(this.headers)
            .payload(this.payload)
            .build();
    }
}