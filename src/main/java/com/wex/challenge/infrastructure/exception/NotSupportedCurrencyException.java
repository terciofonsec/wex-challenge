package com.wex.challenge.infrastructure.exception;

public class NotSupportedCurrencyException extends RuntimeException{
    public NotSupportedCurrencyException(String msg){
        super(msg);
    }
}
