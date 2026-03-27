package com.bank.customer_service.dto.client;

import com.bank.customer_service.dto.request.AccountSyncRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ACCOUNT-SERVICE",
        url = "http://localhost:8082",
        path = "/api/internal/accounts"
)
public interface AccountClient {

    @PostMapping("/create")
    void createAccount(@RequestBody AccountSyncRequest request);
}
