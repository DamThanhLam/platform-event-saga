package org.sento.platform.event.saga.outbox.impl;

import lombok.RequiredArgsConstructor;
import org.sento.platform.event.saga.common.event.EventEnvelope;
import org.sento.platform.event.saga.outbox.OutboxEvent;
import org.sento.platform.event.saga.outbox.OutboxService;
import org.sento.platform.event.saga.outbox.OutboxStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class DefaultOutboxService implements OutboxService {

    private final String sourceService;
    private final int maxAttempts;
    private final OutboxStore outboxStore;

    @Override
    public Mono<String> create(EventEnvelope eventEnvelope, String topic, String messageKey) {
        OutboxEvent event = OutboxEvent.newEvent(
            sourceService,
            eventEnvelope,
            topic,
            messageKey,
            Map.of()
        );

        return outboxStore.save(event)
            .map(OutboxEvent::getId);
    }

    @Override
    public Mono<String> create(
        EventEnvelope eventEnvelope,
        String topic,
        String messageKey,
        Map<String, String> extraHeaders
    ) {
        OutboxEvent event = OutboxEvent.newEvent(
            sourceService,
            eventEnvelope,
            topic,
            messageKey,
            extraHeaders
        );

        return outboxStore.save(event)
            .map(OutboxEvent::getId);
    }

    @Override
    public Flux<OutboxEvent> getNextBatch(String source, int batchSize) {
        return outboxStore.findNextBatch(source, maxAttempts, batchSize);
    }

    @Override
    public Mono<Void> markPublished(String id) {
        return outboxStore.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Outbox not found: " + id)))
            .flatMap(event -> {
                event.markPublished();
                return outboxStore.save(event);
            })
            .then();
    }

    @Override
    public Mono<Void> markFailed(String id, String reason) {
        return outboxStore.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Outbox not found: " + id)))
            .flatMap(event -> {
                event.markFailed(reason);
                return outboxStore.save(event);
            })
            .then();
    }
}
