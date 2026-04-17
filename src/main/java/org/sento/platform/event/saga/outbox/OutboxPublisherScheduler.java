package org.sento.platform.event.saga.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.sento.platform.event.saga.common.event.EventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.batch-size:50}")
    private int batchSize;

    @Value("${platform.event.source-service}")
    private String source;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms:3000}")
    public void publishPendingEvents() {
        outboxService.getNextBatch(source, batchSize)
            .flatMap(entity -> {
                String eventId = entity.getId();

                return Mono.fromCallable(entity::toEnvelope)
                    .flatMap(envelope ->
                        {
                            try {
                                return Mono.fromFuture(
                                    kafkaTemplate.send(
                                        entity.getTopic(),
                                        entity.getMessageKey(),
                                        objectMapper.writeValueAsString(envelope)
                                    )
                                );
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    )
                    .flatMap(result ->
                        outboxService.markPublished(eventId)
                            .thenReturn(result)
                    )
                    .doOnSuccess(result ->
                        log.info("Published outbox event {} to topic {}", eventId, entity.getTopic())
                    )
                    .onErrorResume(ex ->
                        outboxService.markFailed(eventId, ex.getMessage())
                            .then(Mono.fromRunnable(() ->
                                log.error("Failed outbox event {}: {}", eventId, ex.getMessage(), ex)
                            ))
                    );
            })
            .subscribe();
    }
}
