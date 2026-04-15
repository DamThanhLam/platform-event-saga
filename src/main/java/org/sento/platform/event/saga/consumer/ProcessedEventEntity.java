package org.sento.platform.event.saga.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "processed_event")
@Getter
@Setter
@CompoundIndexes({
    @CompoundIndex(
        name = "unique_event_consumer_idx",
        def = "{'event_id': 1, 'consumer_name': 1}",
        unique = true
    )
})
public class ProcessedEventEntity {

    @Field(name = "event_id")
    private String eventId;

    @Field(name = "consumer_name")
    private String consumerName;

    @Field(name = "processed_at")
    private Instant processedAt;
}
