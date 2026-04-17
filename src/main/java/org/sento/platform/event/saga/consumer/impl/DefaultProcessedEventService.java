package org.sento.platform.event.saga.consumer.impl;

import lombok.RequiredArgsConstructor;
import org.sento.platform.event.saga.consumer.ProcessedEventEntity;
import org.sento.platform.event.saga.consumer.ProcessedEventRepository;
import org.sento.platform.event.saga.consumer.ProcessedEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DefaultProcessedEventService implements ProcessedEventService {

    private final ProcessedEventRepository repository;

    @Override
    public Mono<Boolean> existsByEventIdAndConsumerName(String eventId, String consumerName) {
        return repository.existsByEventIdAndConsumerName(eventId, consumerName);
    }

    @Override
    public Mono<String> create(String eventId, String consumerName, Instant processedAt) {
        ProcessedEventEntity entity = new ProcessedEventEntity(
            eventId,
            consumerName,
            processedAt
        );
        return repository.save(entity)
            .map(ProcessedEventEntity::getEventId);
    }
}
