package org.sento.platform.event.saga.consumer.impl;

import lombok.RequiredArgsConstructor;
import org.sento.platform.event.saga.consumer.ProcessedEvent;
import org.sento.platform.event.saga.consumer.ProcessedEventStore;
import org.sento.platform.event.saga.consumer.ProcessedEventService;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RequiredArgsConstructor
public class DefaultProcessedEventService implements ProcessedEventService {

    private final ProcessedEventStore store;

    @Override
    public Mono<Boolean> existsByEventIdAndConsumerName(String eventId, String consumerName) {
        return store.existsByEventIdAndConsumerName(eventId, consumerName);
    }

    @Override
    public Mono<String> create(String eventId, String consumerName, Instant processedAt) {
        ProcessedEvent event = new ProcessedEvent(
            eventId,
            consumerName,
            processedAt
        );
        return store.create(event);
    }
}
