package org.sento.platform.event.saga.registry;

import org.apache.avro.specific.SpecificRecordBase;
import org.sento.platform.event.saga.mapper.AvroEventMapper;
import org.sento.platform.event.saga.outbox.OutboxEvent;

import java.util.Map;

public abstract class AvroMapperRegistry {

    protected final Map<String, AvroEventMapper> registry;

    protected AvroMapperRegistry(Map<String, AvroEventMapper> registry) {
        this.registry = registry;
    }

    public SpecificRecordBase toAvro(OutboxEvent entity) {

        AvroEventMapper mapper = registry.get(entity.getTopic());

        if (mapper == null) {
            throw new IllegalArgumentException(
                "No Avro mapper for eventType=" + entity.getEventType()
            );
        }

        return mapper.toAvro(entity);
    }
}
