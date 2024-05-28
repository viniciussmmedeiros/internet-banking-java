package com.internetbanking.accountapi.kafka;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kafka-service", url = "${kafka.service.url}/kafka")
public interface KafkaClientService {

    @PostMapping("/produce/complete-transaction-request")
    void produceCompleteTransactionRequest(@RequestBody CompleteTransactionRequest request);
}
