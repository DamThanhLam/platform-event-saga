package org.sento.platform.event.saga.producer;

import lombok.RequiredArgsConstructor;
import org.sento.platform.event.saga.outbox.OutboxService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventSagaPublisher {

    private final EventSagaBuilder builder;
    private final OutboxService outboxService;

    public Mono<Void> publishEvent(
        String topic,
        String eventType,
        String aggregateType,
        String aggregateId,
        Object payload
    ) {
        return builder
            .buildEvent(
                eventType,
                aggregateType,
                aggregateId,
                payload
            )
            .flatMap(eventEnvelope -> outboxService.create(
                eventEnvelope,
                topic,
                aggregateId,
                Map.of()
            ))
            .then();
    }
}
