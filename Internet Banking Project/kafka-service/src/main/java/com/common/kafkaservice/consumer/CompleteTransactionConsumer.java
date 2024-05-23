package com.common.kafkaservice.consumer;

import com.common.kafkaservice.client.TransactionServiceClient;
import com.common.kafkaservice.dto.CompleteTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CompleteTransactionConsumer {

    @Autowired
    private TransactionServiceClient transactionClient;

    @KafkaListener(topics = "transaction", groupId = "transaction-consumer-1")
    public void consume(CompleteTransactionRequest request) {
        transactionClient.completeTransaction(request);
    }
}
