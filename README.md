# Saga Platform

A production-style Spring Boot reference project showing:

- REST producer
- Kafka consumer
- Transactional outbox
- Retry with backoff
- Dead-letter topic
- Saga correlation (`correlationId`, `causationId`, `sagaId`, `traceId`)
- Idempotent consumer

## Flow

`POST /api/orders` -> persists an order + outbox event `OrderCreated`

`OrderCreatedConsumer` -> updates order state + writes outbox event `PaymentRequested`

`PaymentRequestedConsumer` -> updates order state + writes outbox event `PaymentCompleted`

Kafka retry is handled by Spring Kafka's `DefaultErrorHandler`. After retries are exhausted, the record is sent to the matching `.DLT` topic.

## Run locally

1. Start infrastructure:
   ```bash
   docker compose up -d
   ```

2. Run the app:
   ```bash
   mvn spring-boot:run
   ```

3. Create an order:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H 'Content-Type: application/json' \
     -H 'X-Correlation-Id: corr-001' \
     -H 'X-Saga-Id: saga-001' \
     -d '{
       "customerId": "CUS-100",
       "amount": 250000,
       "currency": "VND",
       "simulatePaymentFailure": false,
       "items": [
         {
           "productId": "P01",
           "productName": "Keyboard",
           "quantity": 1,
           "unitPrice": 250000
         }
       ]
     }'
   ```

4. Check the database tables:
   - `orders`
   - `outbox_event`
   - `processed_event`

## Demo failure

Set `"simulatePaymentFailure": true` to see retry and then DLT publishing for the payment step.
