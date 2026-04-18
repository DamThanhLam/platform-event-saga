package org.sento.platform.event.saga.outbox;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OutboxStore {

    Mono<OutboxEvent> save(OutboxEvent event);

    Mono<OutboxEvent> findById(String id);

    Flux<OutboxEvent> findNextBatch(String source, int maxAttempts, int batchSize);
}