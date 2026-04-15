package org.sento.platform.event.saga.outbox;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OutboxRepository extends MongoRepository<OutboxEventEntity, String> {

    @Query(value = "{ 'status': { $in: ['NEW', 'FAILED'] }, 'attempts': { $lt: ?1 } }")
    List<OutboxEventEntity> findNextBatch(int maxAttempts);
}