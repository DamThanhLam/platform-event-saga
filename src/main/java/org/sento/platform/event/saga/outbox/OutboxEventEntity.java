package org.sento.platform.event.saga.outbox;

import org.sento.platform.event.saga.common.event.EventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Document(collection = "outbox_event")
@Getter
@Setter
public class OutboxEventEntity {

    @Id
    private String id;

    private String source;

    @Field("aggregate_type")
    private String aggregateType;

    @Field("aggregate_id")
    private String aggregateId;

    @Field("aggregate_version")
    private long aggregateVersion;

    @Field("event_type")
    private String eventType;

    @Field("event_version")
    private int eventVersion;

    private String topic;

    @Field("message_key")
    private String messageKey;

    @Field("correlation_id")
    private String correlationId;

    @Field("causation_id")
    private String causationId;

    @Field("saga_id")
    private String sagaId;

    @Field("trace_id")
    private String traceId;

    @Field("tenant_id")
    private String tenantId;

    private Map<String, String> headers;

    private byte[] payload;

    private OutboxStatus status;

    private int attempts;

    @Field("last_error")
    private String lastError;

    @Field("created_at")
    private Instant createdAt;

    @Field("published_at")
    private Instant publishedAt;

    protected OutboxEventEntity() {}

    public static OutboxEventEntity newEvent(
        String sourceService,
        EventEnvelope event,
        String topic,
        String messageKey,
        Map<String, String> extraHeaders
    ) {
        OutboxEventEntity entity = new OutboxEventEntity();
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

        entity.headers = event.getHeaders() != null
            ? new LinkedHashMap<>(event.getHeaders())
            : new LinkedHashMap<>();

        if (extraHeaders != null && !extraHeaders.isEmpty()) {
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