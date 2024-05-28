package com.common.kafkaservice.controller;

import com.common.kafkaservice.dto.BalanceUpdateRequest;
import com.common.kafkaservice.dto.CompleteTransactionRequest;
import com.common.kafkaservice.producer.BalanceUpdateProducer;
import com.common.kafkaservice.producer.CompleteTransactionProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafka")
public class KafkaController {

    @Autowired
    private BalanceUpdateProducer balanceUpdateProducer;

    @Autowired
    private CompleteTransactionProducer completeTransactionProducer;

    @PostMapping("/produce/update-balance-request")
    public void updateBalanceRequest(@RequestBody BalanceUpdateRequest request) {
        balanceUpdateProducer.produce(request);
    }

    @PostMapping("/produce/complete-transaction-request")
    public void completeTransactionRequest(@Valid @RequestBody CompleteTransactionRequest request) {
        completeTransactionProducer.produce(request);
    }
}
