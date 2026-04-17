package org.sento.platform.event.saga.consumer;

import reactor.core.publisher.Mono;

import java.time.Instant;

public interface ProcessedEventService {
    Mono<Boolean> existsByEventIdAndConsumerName(String eventId, String consumerName);
    Mono<String> create(String eventId, String consumerName, Instant processedAt);
}
