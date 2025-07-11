package com.wex.challenge.application.service;

import com.wex.challenge.application.dto.PurchaseResponse;
import com.wex.challenge.application.port.in.CreatePurchaseCommand;
import com.wex.challenge.application.port.out.PurchaseRepository;
import com.wex.challenge.domain.model.Purchase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CreatePurchaseUseCase {
    private final PurchaseRepository purchaseRepository;

    public PurchaseResponse execute(CreatePurchaseCommand command) {
        Purchase purchase = Purchase.newPurchase(
                command.getDescription(),
                command.getTransactionDate(),
                command.getPurchaseAmountUsd()
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        log.info("Purchase created id={}", savedPurchase.getId());

        return new PurchaseResponse(
                savedPurchase.getId().getValue(),
                savedPurchase.getDescription().value(),
                savedPurchase.getTransactionDate().value(),
                savedPurchase.getPurchaseAmountUsd().value()
        );
    }
}
