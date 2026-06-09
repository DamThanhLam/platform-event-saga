package org.sento.platform.event.saga.producer;

import lombok.RequiredArgsConstructor;
import org.sento.platform.event.saga.common.event.EventEnvelope;
import org.sento.platform.event.saga.common.event.EventEnvelopeFactory;
import org.sento.platform.logging.context.CorrelationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EventSagaBuilder {

    private final EventEnvelopeFactory eventEnvelopeFactory;

    public Mono<EventEnvelope> buildEvent(
        String eventType,
        String aggregateType,
        String aggregateId,
        Object payload
    ) {
        return Mono.fromCallable(() ->
            eventEnvelopeFactory.create(
                eventType,
                aggregateType,
                aggregateId,
                CorrelationContext.causationId(),
                payload
            )
        );
    }
}
