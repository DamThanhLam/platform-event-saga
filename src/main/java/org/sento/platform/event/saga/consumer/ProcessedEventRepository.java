package org.sento.platform.event.saga.consumer;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProcessedEventRepository extends ReactiveCrudRepository<ProcessedEventEntity, String> {
    Mono<Boolean> existsByEventIdAndConsumerName(String eventId, String consumerName);
}
