package com.stamina_inc.academiq_auth.messaging;

import com.stamina_inc.academiq_auth.domain.dto.MemberPayload;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MemberProducer {

    private final KafkaTemplate<String, MemberPayload> kafkaTemplate;

    public MemberProducer(KafkaTemplate<String, MemberPayload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicRegisteredMember(MemberPayload memberPayload) {
        kafkaTemplate.send(KafkaTopics.MEMBER_REGISTERED.value(), memberPayload);
    }

}
