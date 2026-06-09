package org.sento.platform.event.saga.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.sento.platform.event.saga.outbox.OutboxEvent;

public interface AvroEventMapper {

    SpecificRecordBase toAvro(OutboxEvent entity);
}
