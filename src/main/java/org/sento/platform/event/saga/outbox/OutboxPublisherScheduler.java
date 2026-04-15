package org.sento.platform.event.saga.outbox;

import org.sento.platform.event.saga.common.event.PlatformEventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.batch-size:50}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms:3000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> batch = outboxRepository.findNextBatch(batchSize);
        for (OutboxEventEntity entity : batch) {
            try {
                PlatformEventEnvelope envelope = entity.toEnvelope();
                kafkaTemplate.send(entity.getTopic(), entity.getMessageKey(), objectMapper.writeValueAsString(envelope)).get();
                entity.markPublished();
                log.info("Published outbox event {} to topic {}", entity.getId(), entity.getTopic());
            } catch (Exception ex) {
                entity.markFailed(ex.getMessage());
                log.error("Failed to publish outbox event {}: {}", entity.getId(), ex.getMessage(), ex);
            }
        }
    }
}
