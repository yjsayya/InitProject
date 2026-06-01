package init.project.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    // ── 공통 ──────────────────────────────────────────────
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // ── Producer 단축 속성 ────────────────────────────────
    @Value("${spring.kafka.producer.client-id}")
    private String producerClientId;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.retries}")
    private int retries;

    @Value("${spring.kafka.producer.batch-size}")
    private int batchSize;

    @Value("${spring.kafka.producer.buffer-memory}")
    private long bufferMemory;

    @Value("${spring.kafka.producer.compression-type}")
    private String compressionType;

    // ── Producer properties ───────────────────────────────
    @Value("${spring.kafka.producer.properties.linger.ms}")
    private int lingerMs;

    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private int deliveryTimeoutMs;

    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private int requestTimeoutMs;

    @Value("${spring.kafka.producer.properties.max.block.ms}")
    private int maxBlockMs;

    @Value("${spring.kafka.producer.properties.max.request.size}")
    private int maxRequestSize;

    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private int maxInFlightRequestsPerConnection;

    @Value("${spring.kafka.producer.properties.connections.max.idle.ms}")
    private long connectionsMaxIdleMs;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private boolean enableIdempotence;

    @Value("${spring.kafka.producer.properties.metadata.max.age.ms}")
    private int metadataMaxAgeMs;

    @Value("${spring.kafka.producer.properties.metadata.max.idle.ms}")
    private int metadataMaxIdleMs;

    @Value("${spring.kafka.producer.properties.retry.backoff.ms}")
    private int producerRetryBackoffMs;

    @Value("${spring.kafka.producer.properties.retry.backoff.max.ms}")
    private int producerRetryBackoffMaxMs;

    @Value("${spring.kafka.producer.properties.reconnect.backoff.ms}")
    private int producerReconnectBackoffMs;

    @Value("${spring.kafka.producer.properties.reconnect.backoff.max.ms}")
    private int producerReconnectBackoffMaxMs;

    @Value("${spring.kafka.producer.properties.send.buffer.bytes}")
    private int producerSendBufferBytes;

    @Value("${spring.kafka.producer.properties.receive.buffer.bytes}")
    private int producerReceiveBufferBytes;

    @Value("${spring.kafka.producer.properties.client.dns.lookup}")
    private String producerClientDnsLookup;

    // ── Consumer 단축 속성 ────────────────────────────────
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.client-id}")
    private String consumerClientId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;

    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private int autoCommitInterval;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private int maxPollRecords;

    @Value("${spring.kafka.consumer.fetch-min-size}")
    private int fetchMinSize;

    @Value("${spring.kafka.consumer.fetch-max-wait}")
    private int fetchMaxWait;

    @Value("${spring.kafka.consumer.isolation-level}")
    private String isolationLevel;

    // ── Consumer properties ───────────────────────────────
    @Value("${spring.kafka.consumer.properties.fetch.max.bytes}")
    private int fetchMaxBytes;

    @Value("${spring.kafka.consumer.properties.max.partition.fetch.bytes}")
    private int maxPartitionFetchBytes;

    @Value("${spring.kafka.consumer.properties.max.poll.interval.ms}")
    private int maxPollIntervalMs;

    @Value("${spring.kafka.consumer.properties.default.api.timeout.ms}")
    private int defaultApiTimeoutMs;

    @Value("${spring.kafka.consumer.properties.session.timeout.ms}")
    private int sessionTimeoutMs;

    @Value("${spring.kafka.consumer.properties.heartbeat.interval.ms}")
    private int heartbeatIntervalMs;

    @Value("${spring.kafka.consumer.properties.connections.max.idle.ms}")
    private long consumerConnectionsMaxIdleMs;

    @Value("${spring.kafka.consumer.properties.metadata.max.age.ms}")
    private int consumerMetadataMaxAgeMs;

    @Value("${spring.kafka.consumer.properties.retry.backoff.ms}")
    private int consumerRetryBackoffMs;

    @Value("${spring.kafka.consumer.properties.retry.backoff.max.ms}")
    private int consumerRetryBackoffMaxMs;

    @Value("${spring.kafka.consumer.properties.reconnect.backoff.ms}")
    private int consumerReconnectBackoffMs;

    @Value("${spring.kafka.consumer.properties.reconnect.backoff.max.ms}")
    private int consumerReconnectBackoffMaxMs;

    @Value("${spring.kafka.consumer.properties.send.buffer.bytes}")
    private int consumerSendBufferBytes;

    @Value("${spring.kafka.consumer.properties.receive.buffer.bytes}")
    private int consumerReceiveBufferBytes;

    @Value("${spring.kafka.consumer.properties.client.dns.lookup}")
    private String consumerClientDnsLookup;

    @Value("${spring.kafka.consumer.properties.allow.auto.create.topics}")
    private boolean allowAutoCreateTopics;

    @Value("${spring.kafka.consumer.properties.exclude.internal.topics}")
    private boolean excludeInternalTopics;

    @Value("${spring.kafka.consumer.properties.check.crcs}")
    private boolean checkCrcs;

    @Value("${spring.kafka.consumer.properties.partition.assignment.strategy}")
    private String partitionAssignmentStrategy;

    // ── Listener ──────────────────────────────────────────
    @Value("${spring.kafka.listener.concurrency}")
    private int concurrency;

    @Value("${spring.kafka.listener.poll-timeout}")
    private long pollTimeout;


    // ════════════════════════════════════════
    // PRODUCER
    // ════════════════════════════════════════

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // ── 단축 속성 ─────────────────────────────────────
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, producerClientId);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.RETRIES_CONFIG, retries);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);

        // ── properties 값들 ───────────────────────────────
        config.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnection);
        config.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, connectionsMaxIdleMs);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        config.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, metadataMaxAgeMs);
        config.put(ProducerConfig.METADATA_MAX_IDLE_CONFIG, metadataMaxIdleMs);
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, producerRetryBackoffMs);
        config.put(ProducerConfig.RETRY_BACKOFF_MAX_MS_CONFIG, producerRetryBackoffMaxMs);
        config.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, producerReconnectBackoffMs);
        config.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, producerReconnectBackoffMaxMs);
        config.put(ProducerConfig.SEND_BUFFER_CONFIG, producerSendBufferBytes);
        config.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, producerReceiveBufferBytes);
        config.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG, producerClientDnsLookup);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    // ════════════════════════════════════════
    // CONSUMER
    // ════════════════════════════════════════

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        // ── 단축 속성 ─────────────────────────────────────
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerClientId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinSize);
        config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWait);
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);

        // ── properties 값들 ───────────────────────────────
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes);
        config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        config.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, defaultApiTimeoutMs);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);
        config.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, consumerConnectionsMaxIdleMs);
        config.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, consumerMetadataMaxAgeMs);
        config.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, consumerRetryBackoffMs);
        config.put(ConsumerConfig.RETRY_BACKOFF_MAX_MS_CONFIG, consumerRetryBackoffMaxMs);
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, consumerReconnectBackoffMs);
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, consumerReconnectBackoffMaxMs);
        config.put(ConsumerConfig.SEND_BUFFER_CONFIG, consumerSendBufferBytes);
        config.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, consumerReceiveBufferBytes);
        config.put(ConsumerConfig.CLIENT_DNS_LOOKUP_CONFIG, consumerClientDnsLookup);
        config.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, allowAutoCreateTopics);
        config.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, excludeInternalTopics);
        config.put(ConsumerConfig.CHECK_CRCS_CONFIG, checkCrcs);
        config.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, partitionAssignmentStrategy);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

}