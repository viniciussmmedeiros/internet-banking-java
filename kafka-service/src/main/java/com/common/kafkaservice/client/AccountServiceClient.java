package com.common.kafkaservice.client;

import com.common.kafkaservice.dto.BalanceUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", url = "${account.service.url}/accounts")
public interface AccountServiceClient {

    @PutMapping("/update-balance")
    void updateBalance(@RequestBody BalanceUpdateRequest request);
}
