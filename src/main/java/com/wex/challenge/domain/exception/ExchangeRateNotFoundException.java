package com.wex.challenge.domain.exception;

public class ExchangeRateNotFoundException extends RuntimeException {

    public ExchangeRateNotFoundException(String msg){
        super(msg);
    }
}
