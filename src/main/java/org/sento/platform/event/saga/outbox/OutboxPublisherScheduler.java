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
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
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

    private static final int CONCURRENCY = 10;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms:3000}")
    public void publishPendingEvents() {
        log.info("Outbox scheduler start");
        outboxService.getNextBatch(source, batchSize)
            .flatMap(this::processEvent, CONCURRENCY)
            .doOnError(ex -> log.error("Outbox scheduler failed", ex))
            .subscribe();
    }

    private Mono<Void> processEvent(OutboxEvent entity) {
        String eventId = entity.getId();

        return Mono.fromCallable(entity::toEnvelope)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::serialize)
            .flatMap(payload ->
                Mono.fromFuture(
                    kafkaTemplate.send(
                        entity.getTopic(),
                        entity.getMessageKey(),
                        payload
                    )
                )
            )

            .timeout(Duration.ofSeconds(5))

            .flatMap(result ->
                outboxService.markPublished(eventId)
            )

            .doOnSuccess(v ->
                log.info("Published outbox event {} to topic {}", eventId, entity.getTopic())
            )

            .retryWhen(
                Retry.backoff(3, Duration.ofMillis(200))
                    .filter(this::isRetryable)
                    .onRetryExhaustedThrow((spec, signal) ->
                        signal.failure()
                    )
            )

            .onErrorResume(ex ->
                outboxService.markFailed(eventId, ex.getMessage())
                    .doOnSuccess(v ->
                        log.error("Failed outbox event {}: {}", eventId, ex.getMessage(), ex)
                    )
            );
    }

    private Mono<String> serialize(EventEnvelope envelope) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(envelope))
            .subscribeOn(Schedulers.boundedElastic());
    }

    private boolean isRetryable(Throwable ex) {
        return !(ex instanceof JsonProcessingException);
    }
}