package com.common.kafkaservice.consumer;

import com.common.kafkaservice.client.AccountServiceClient;
import com.common.kafkaservice.dto.BalanceUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BalanceUpdateConsumer {

    @Autowired
    private AccountServiceClient accountClient;

    @KafkaListener(topics = "balance-update", groupId = "balance-update-consumer-1")
    public void consume(BalanceUpdateRequest request) {
        accountClient.updateBalance(request);
    }
}
