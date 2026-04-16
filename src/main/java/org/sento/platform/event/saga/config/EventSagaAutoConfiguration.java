package org.sento.platform.event.saga.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(EventSagaProperties.class)
@ComponentScan(basePackages = "org.sento.platform.event.saga")
@Import(KafkaConfig.class)
@ConditionalOnProperty(prefix = "platform.event", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EventSagaAutoConfiguration {
}
