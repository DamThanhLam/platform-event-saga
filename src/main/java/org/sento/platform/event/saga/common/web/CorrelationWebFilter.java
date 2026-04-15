package org.sento.platform.event.saga.common.web;

import org.sento.platform.event.saga.common.event.CorrelationContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CorrelationWebFilter implements WebFilter {

    private final CorrelationContext correlationContext;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return Mono.defer(() -> {
            try {
                putIfAbsent("correlationId", exchange.getRequest().getHeaders().getFirst("X-Correlation-Id"));
                putIfAbsent("causationId", exchange.getRequest().getHeaders().getFirst("X-Causation-Id"));
                putIfAbsent("sagaId", exchange.getRequest().getHeaders().getFirst("X-Saga-Id"));
                putIfAbsent("traceId", exchange.getRequest().getHeaders().getFirst("X-Trace-Id"));
                putIfAbsent("tenantId", exchange.getRequest().getHeaders().getFirst("X-Tenant-Id"));

                exchange.getResponse().getHeaders().set("X-Correlation-Id", correlationContext.correlationId());
                exchange.getResponse().getHeaders().set("X-Saga-Id", correlationContext.sagaId());
                exchange.getResponse().getHeaders().set("X-Trace-Id", correlationContext.traceId());

                return chain.filter(exchange);

            } finally {
                MDC.clear();
            }
        });
    }

    private void putIfAbsent(String key, String value) {
        if (value == null || value.isBlank()) {
            MDC.put(key, UUID.randomUUID().toString());
            return;
        }
        MDC.put(key, value);
    }
}