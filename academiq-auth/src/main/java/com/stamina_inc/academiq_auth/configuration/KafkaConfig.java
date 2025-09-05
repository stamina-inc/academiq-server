package com.stamina_inc.academiq_auth.configuration;

import com.stamina_inc.academiq_auth.domain.dto.MemberPayload;
import com.stamina_inc.academiq_auth.messaging.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.url}")
    private String kafkaUrl;

    @Bean
    public Map<String, Object> producerConfiguration() {
        return Map.of(
                org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl,
                org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG, "auth",
                org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all",
                org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true
        );
    }

    @Bean
    public ProducerFactory<String, MemberPayload> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                producerConfiguration(),
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, MemberPayload> kafkaTemplate(
            ProducerFactory<String, MemberPayload> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public NewTopic memberRegisteredTopic() {
        return TopicBuilder
                .name(KafkaTopics.MEMBER_REGISTERED.value())
                .partitions(1)
                .replicas(1)
                .build();
    }
}