package com.wex.challenge.infrastructure.adapter.web;

import com.wex.challenge.application.dto.ConvertedPurchaseResponse;
import com.wex.challenge.application.dto.PurchaseResponse;
import com.wex.challenge.application.port.in.CreatePurchaseCommand;
import com.wex.challenge.application.port.in.RetrieveConvertedPurchaseQuery;
import com.wex.challenge.application.service.CreatePurchaseUseCase;
import com.wex.challenge.application.service.RetrieveConvertedPurchaseUseCase;
import com.wex.challenge.domain.exception.ExchangeRateNotFoundException;
import com.wex.challenge.domain.exception.InvalidPurchaseException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequestMapping("/purchases")
@RestController
@AllArgsConstructor
@Slf4j
public class PurchaseController {
    private final CreatePurchaseUseCase createPurchaseUseCase;
    private final RetrieveConvertedPurchaseUseCase retrieveConvertedPurchaseUseCase;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody CreatePurchaseCommand command) {
        log.info("Creating purchase, command:{}", command);
        return new ResponseEntity<>(createPurchaseUseCase.execute(command), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{purchaseId}/converted/{targetCurrencyCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConvertedPurchaseResponse> getConvertedPurchase(
            @PathVariable String purchaseId,
            @PathVariable String targetCurrencyCode) {

        RetrieveConvertedPurchaseQuery query = new RetrieveConvertedPurchaseQuery();
        query.setPurchaseId(purchaseId);
        query.setTargetCurrencyCode(targetCurrencyCode);
        log.info("Retrieve purchase converted, purchaseId={}, targetCurrencyCode={}", purchaseId, targetCurrencyCode);
        return new ResponseEntity<>(retrieveConvertedPurchaseUseCase.execute(query), HttpStatus.OK);
    }


    @ExceptionHandler(InvalidPurchaseException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPurchaseException(InvalidPurchaseException ex, WebRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                req.getDescription(false).replace("uri=", ""),
                null

        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex, WebRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                req.getDescription(false).replace("uri=", ""),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExchangeRateNotFoundException(ExchangeRateNotFoundException ex, WebRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                req.getDescription(false).replace("uri=", ""),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                req.getDescription(false).replace("uri=", ""),
                null

        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
