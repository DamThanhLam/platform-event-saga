package org.sento.platform.event.saga.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@AutoConfiguration
@EnableConfigurationProperties(EventSagaProperties.class)
@ComponentScan(basePackages = "org.sento.platform.event.saga")
@EnableReactiveMongoRepositories(
    basePackages = {
        "org.sento.platform.event.saga.outbox",
        "org.sento.platform.event.saga.consumer"
    }
)
@Import(KafkaConfig.class)
@ConditionalOnProperty(prefix = "platform.event", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EventSagaAutoConfiguration {
}
