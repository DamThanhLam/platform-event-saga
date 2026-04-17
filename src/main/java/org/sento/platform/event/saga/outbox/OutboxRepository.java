package org.sento.platform.event.saga.outbox;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OutboxRepository extends ReactiveCrudRepository<OutboxEventEntity, String> {

    @Query("""
    {
      'source': ?0,
      'status': { $in: ['NEW', 'FAILED'] },
      'attempts': { $lt: ?1 }
    }
    """)
    Flux<OutboxEventEntity> findNextBatchBySource(String source, int maxAttempts);
}