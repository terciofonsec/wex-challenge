package com.wex.challenge.infrastructure.adapter.external;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "treasury-api", url = "${treasury.api.base-url}")
public interface TreasuryExchangeRateClient {
    @GetMapping
    TreasuryExchangeRateResponse getExchangeRates(
            @RequestParam("filter") String filter,
            @RequestParam("sort") String sort,
            @RequestParam("page[size]") int pageSize
    );
}
