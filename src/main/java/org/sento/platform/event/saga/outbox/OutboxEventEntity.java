package org.sento.platform.event.saga.outbox;

import org.sento.platform.event.saga.common.event.PlatformEventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
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

    private JsonNode payload;

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
        PlatformEventEnvelope event,
        String topic,
        String messageKey,
        Map<String, String> extraHeaders
    ) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.id = event.eventId();
        entity.aggregateType = event.aggregateType();
        entity.aggregateId = event.aggregateId();
        entity.aggregateVersion = event.aggregateVersion();
        entity.eventType = event.eventType();
        entity.eventVersion = event.eventVersion();
        entity.topic = topic;
        entity.messageKey = messageKey;
        entity.correlationId = event.correlationId();
        entity.causationId = event.causationId();
        entity.sagaId = event.sagaId();
        entity.traceId = event.traceId();
        entity.tenantId = event.tenantId();

        entity.headers = event.headers();
        if (extraHeaders != null && !extraHeaders.isEmpty()) {
            entity.headers = new LinkedHashMap<>(entity.headers);
            entity.headers.putAll(extraHeaders);
        }

        entity.payload = event.payload();
        entity.status = OutboxStatus.NEW;
        entity.attempts = 0;
        entity.createdAt = event.occurredAt();

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

    public PlatformEventEnvelope toEnvelope() {
        return new PlatformEventEnvelope(
            id,
            eventType,
            eventVersion,
            createdAt,
            "saga-platform",
            aggregateType,
            aggregateId,
            aggregateVersion,
            correlationId,
            causationId,
            sagaId,
            traceId,
            tenantId,
            headers,
            payload
        );
    }
}