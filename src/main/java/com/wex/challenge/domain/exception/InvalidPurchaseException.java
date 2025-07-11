package com.wex.challenge.domain.exception;

public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException(String msg) {
        super(msg);
    }
}
