package com.common.kafkaservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic transactionProcessTopicBuilder() {
        return TopicBuilder.name("transaction-process").build();
    }

    @Bean
    public NewTopic balanceUpdateTopicBuilder() {
        return TopicBuilder.name("balance-update").build();
    }
}
