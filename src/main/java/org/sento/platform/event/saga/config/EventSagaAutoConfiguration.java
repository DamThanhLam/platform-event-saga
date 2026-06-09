package org.sento.platform.event.saga.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sento.platform.event.saga.outbox.OutboxPublisherScheduler;
import org.sento.platform.event.saga.outbox.OutboxService;
import org.sento.platform.event.saga.registry.AvroMapperRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;

@RetryableTopic
@AutoConfiguration
@EnableConfigurationProperties(EventSagaProperties.class)
@ComponentScan(basePackages = "org.sento.platform.event.saga")
@Import(KafkaConfig.class)
@ConditionalOnProperty(prefix = "platform.event", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EventSagaAutoConfiguration {


    @ConditionalOnProperty(
        prefix = "platform.event.outbox",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public OutboxPublisherScheduler outboxPublisherScheduler(
        OutboxService outboxService,
        AvroMapperRegistry mapperRegistry,
        KafkaTemplate<String, Object> kafkaTemplate,
        ObjectMapper objectMapper
    ) {
        return new OutboxPublisherScheduler(outboxService, mapperRegistry, kafkaTemplate, objectMapper);
    }
}
