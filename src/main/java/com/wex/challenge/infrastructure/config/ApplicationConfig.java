package com.wex.challenge.infrastructure.config;

import com.wex.challenge.application.port.out.ExchangeRateProvider;
import com.wex.challenge.domain.service.CurrencyConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public CurrencyConversionService currencyConversionService(ExchangeRateProvider exchangeRateProvider) {
        return new CurrencyConversionService(exchangeRateProvider);
    }
}
