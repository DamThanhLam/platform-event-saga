package org.sento.platform.event.saga.common.event;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class CorrelationContext {

    public String correlationId() {
        return ensure("correlationId");
    }

    public String causationId() {
        return ensure("causationId");
    }

    public String sagaId() {
        return ensure("sagaId");
    }

    public String traceId() {
        return ensure("traceId");
    }

    public String tenantId() {
        return value("tenantId", null);
    }

    public void set(String key, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(key, value);
        }
    }

    public void clear() {
        MDC.clear();
    }

    private String ensure(String key) {
        String value = MDC.get(key);
        if (!StringUtils.hasText(value)) {
            value = UUID.randomUUID().toString();
            MDC.put(key, value);
        }
        return value;
    }

    private String value(String key, String defaultValue) {
        String value = MDC.get(key);
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}
