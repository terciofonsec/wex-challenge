package com.wex.challenge.domain.model;

import java.util.Objects;
import java.util.UUID;


public class PurchaseId {

    private final String value;

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseId that = (PurchaseId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    private PurchaseId(String value){
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException("PurchaseId value cannot be null or empty");
        }
        this.value = value;
    }

    public static PurchaseId generate(){
        return new PurchaseId(UUID.randomUUID().toString());
    }

    public static PurchaseId from(String value){
        return new PurchaseId(value);
    }
}
