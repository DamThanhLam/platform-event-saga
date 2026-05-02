package org.sento.platform.event.saga.common.event;

import lombok.RequiredArgsConstructor;
import org.sento.platform.event.saga.config.EventSagaProperties;
import org.sento.platform.event.saga.serializer.AvroSerializer;
import org.sento.platform.logging.context.CorrelationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventEnvelopeFactory {

    private final EventSagaProperties eventSagaProperties;

    public <T extends org.apache.avro.specific.SpecificRecord> EventEnvelope create(
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

    public <T extends org.apache.avro.specific.SpecificRecord> EventEnvelope create(
        String eventType,
        String aggregateType,
        String aggregateId,
        String causationId,
        T payload
    ) {
        return create(
            eventType,
            1,
            aggregateType,
            aggregateId,
            1,
            causationId,
            Map.of(),
            payload
        );
    }

    public <T extends org.apache.avro.specific.SpecificRecord> EventEnvelope create(
        String eventType,
        int eventVersion,
        String aggregateType,
        String aggregateId,
        long aggregateVersion,
        String causationId,
        Map<String, String> headers,
        T payload
    ) {

        return EventEnvelope.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(eventType)
            .eventVersion(eventVersion)
            .occurredAt(Instant.now())
            .sourceService(eventSagaProperties.getSourceService())

            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .aggregateVersion(aggregateVersion)

            .correlationId(CorrelationContext.correlationId())
            .causationId(causationId)
            .sagaId(CorrelationContext.sagaId())
            .traceId(CorrelationContext.traceId())
            .tenantId(CorrelationContext.tenantId())

            .headers(safeHeaders(headers))
            .payload(AvroSerializer.toBytes(payload))
            .build();
    }

    private Map<String, String> safeHeaders(Map<String, String> headers) {
        return headers == null ? Map.of() : Map.copyOf(headers);
    }
}