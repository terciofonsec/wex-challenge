package com.wex.challenge.application.port.out;

import com.wex.challenge.domain.model.Purchase;
import com.wex.challenge.domain.model.PurchaseId;

import java.util.Optional;

public interface PurchaseRepository {
    Purchase save(Purchase purchase);
    Optional<Purchase> findById(PurchaseId id);
}
