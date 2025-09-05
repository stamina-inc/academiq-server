package com.stamina_inc.academiq_auth.messaging;

public enum KafkaTopics {
    MEMBER_REGISTERED("member.registered");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }

    public String value() {
        return topicName;
    }
}