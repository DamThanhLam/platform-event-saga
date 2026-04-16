package org.sento.platform.event.saga.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "platform.event")
public class EventSagaProperties {
    private String sourceService = "saga-platform";
    private Map<String, Integer> eventVersions;
    private Map<String, Integer> aggregateVersions;

    public Map<String, Integer> getEventVersions() {
        if (eventVersions == null) {
            this.eventVersions = new HashMap<>();
        }
        return this.eventVersions;
    }


    public Map<String, Integer> getAggregateVersions() {
        if (aggregateVersions == null) {
            this.aggregateVersions = new HashMap<>();
        }
        return this.aggregateVersions;
    }
}
