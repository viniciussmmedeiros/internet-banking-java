package com.common.kafkaservice.producer;

import com.common.kafkaservice.dto.BalanceUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BalanceUpdateProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void produce(BalanceUpdateRequest request) {
        kafkaTemplate.send("balance-update", request);
    }
}
