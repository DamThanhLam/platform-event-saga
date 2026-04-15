package org.sento.platform.event.saga.common.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "platform.event")
public class PlatformProperties {
    private String sourceService = "saga-platform";
}
