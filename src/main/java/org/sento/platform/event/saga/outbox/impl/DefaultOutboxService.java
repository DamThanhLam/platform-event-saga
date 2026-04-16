package org.sento.platform.event.saga.outbox.impl;

import lombok.AllArgsConstructor;
import org.sento.platform.event.saga.common.event.EventEnvelope;
import org.sento.platform.event.saga.outbox.OutboxEventEntity;
import org.sento.platform.event.saga.outbox.OutboxRepository;
import org.sento.platform.event.saga.outbox.OutboxService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@AllArgsConstructor
public class DefaultOutboxService implements OutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    public Mono<String> create(
        EventEnvelope eventEnvelope,
        String topic,
        String messageKey,
        Map<String, String> extraHeaders
    ) {
        OutboxEventEntity entity = OutboxEventEntity.newEvent(
            eventEnvelope,
            topic,
            messageKey,
            extraHeaders
        );
        return outboxRepository.save(entity)
            .map(OutboxEventEntity::getId);
    }

    @Override
    public Flux<OutboxEventEntity> getNextBatch(String source, int batchSize) {
        return outboxRepository
            .findNextBatchBySource(source, batchSize)
            .take(batchSize);
    }

    @Override
    public Mono<Void> markPublished(String id) {
        return outboxRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Outbox not found: " + id)))
            .flatMap(entity -> {
                entity.markPublished();
                return outboxRepository.save(entity);
            })
            .then();
    }

    @Override
    public Mono<Void> markFailed(String id, String reason) {
        return outboxRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Outbox not found: " + id)))
            .flatMap(entity -> {
                entity.markFailed(reason);
                return outboxRepository.save(entity);
            })
            .then();
    }
}
