package org.sento.platform.event.saga.consumer;


import reactor.core.publisher.Mono;

public interface ProcessedEventStore {
    Mono<Boolean> existsByEventIdAndConsumerName(String eventId, String consumerName);
    Mono<String> create(ProcessedEvent event);
}
