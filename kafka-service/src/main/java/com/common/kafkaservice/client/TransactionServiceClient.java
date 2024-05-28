package com.common.kafkaservice.client;

import com.common.kafkaservice.dto.CompleteTransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service", url = "${transaction.service.url}/transactions")
public interface TransactionServiceClient {

    @PostMapping("/complete-transaction")
    void completeTransaction(@RequestBody CompleteTransactionRequest request);
}
