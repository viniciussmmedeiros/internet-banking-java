package com.internetbanking.transactionapi.kafka;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kafka-service", url = "${kafka.service.url}/kafka")
public interface KafkaServiceClient {

    @PostMapping("/produce/update-balance-request")
    void produceBalanceUpdateRequest(@RequestBody BalanceUpdateRequest request);
}
