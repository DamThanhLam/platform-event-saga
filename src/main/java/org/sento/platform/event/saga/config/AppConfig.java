package org.sento.platform.event.saga.config;

import org.sento.platform.event.saga.common.event.PlatformProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PlatformProperties.class)
public class AppConfig {
}
