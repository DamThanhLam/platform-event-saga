package org.sento.platform.event.saga.outbox;

import org.sento.platform.event.saga.common.event.EventEnvelope;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OutboxService {

    Mono<String> create(
        EventEnvelope eventEnvelope,
        String topic,
        String messageKey
    );

    Mono<String> create(
        EventEnvelope eventEnvelope,
        String topic,
        String messageKey,
        Map<String, String> extraHeaders
    );

    Flux<OutboxEvent> getNextBatch(String source, int batchSize);

    Mono<Void> markPublished(String id);

    Mono<Void> markFailed(String id, String reason);
}
