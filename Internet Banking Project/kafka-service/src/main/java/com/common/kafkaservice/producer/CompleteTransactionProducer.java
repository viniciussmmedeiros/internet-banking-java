package com.common.kafkaservice.producer;

import com.common.kafkaservice.dto.CompleteTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CompleteTransactionProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void produce(CompleteTransactionRequest request) {
        kafkaTemplate.send("transaction", request);
    }
}
