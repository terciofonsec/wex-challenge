package com.wex.challenge.infrastructure.adapter.persistence;

import com.wex.challenge.application.port.out.PurchaseRepository;
import com.wex.challenge.domain.model.Purchase;
import com.wex.challenge.domain.model.PurchaseId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PurchaseRepositoryAdapter implements PurchaseRepository {

    private final PurchaseJpaRepository purchaseJpaRepository;

    @Override
    public Purchase save(Purchase purchase) {
        PurchaseJpaEntity jpaEntity = new PurchaseJpaEntity(
                purchase.getId().getValue(),
                purchase.getDescription().value(),
                purchase.getTransactionDate().value(),
                purchase.getPurchaseAmountUsd().value()
        );
        PurchaseJpaEntity savedEntity = purchaseJpaRepository.save(jpaEntity);
        return Purchase.from(
                savedEntity.getId(),
                savedEntity.getDescription(),
                savedEntity.getTransactionDate(),
                savedEntity.getPurchaseAmountUsd()
        );
    }

    @Override
    public Optional<Purchase> findById(PurchaseId id) {
        return purchaseJpaRepository.findById(id.getValue())
                .map(jpaEntity -> Purchase.from(
                        jpaEntity.getId(),
                        jpaEntity.getDescription(),
                        jpaEntity.getTransactionDate(),
                        jpaEntity.getPurchaseAmountUsd()
                ));
    }
}
