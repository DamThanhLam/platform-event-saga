package org.sento.platform.event.saga.consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class ProcessedEvent {
    private String eventId;
    private String consumerName;
    private Instant processedAt;
}
