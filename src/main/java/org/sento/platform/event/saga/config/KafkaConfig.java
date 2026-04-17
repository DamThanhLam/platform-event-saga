package org.sento.platform.event.saga.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();

        putIfNotNull(props, ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            env.getProperty("spring.kafka.bootstrap-servers"));

        putIfNotNull(props, ProducerConfig.ACKS_CONFIG,
            env.getProperty("spring.kafka.producer.acks", "all"));

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        putIfNotNull(props, ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
            env.getProperty("spring.kafka.producer.enable-idempotence")); // note the kebab-case

        String linger = env.getProperty("spring.kafka.producer.linger-ms", "5");
        props.put(ProducerConfig.LINGER_MS_CONFIG, Long.parseLong(linger));

        String retries = env.getProperty("spring.kafka.producer.retries", "10");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.parseInt(retries));

        return new DefaultKafkaProducerFactory<>(props);
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();

        putIfNotNull(props, ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            env.getProperty("spring.kafka.bootstrap-servers"));

        putIfNotNull(props, ConsumerConfig.GROUP_ID_CONFIG,
            env.getProperty("spring.kafka.consumer.group-id"));

        putIfNotNull(props, ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            env.getProperty("spring.kafka.consumer.auto-offset-reset", "earliest"));

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
        ConsumerFactory<Object, Object> consumerFactory,
        KafkaTemplate<String, String> kafkaTemplate,
        ConcurrentKafkaListenerContainerFactoryConfigurer configurer
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, (ConsumerRecord<?, ?> record, Exception ex) -> {
            String dltTopic = record.topic().endsWith(".DLT") ? record.topic() : record.topic() + ".DLT";
            return new TopicPartition(dltTopic, record.partition());
        });

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
        errorHandler.addRetryableExceptions(IllegalStateException.class, RuntimeException.class);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
