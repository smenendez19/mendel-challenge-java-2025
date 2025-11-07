package com.mendel.mendel_challenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mendel.mendel_challenge.dto.GetSumTransactionResponse;
import com.mendel.mendel_challenge.dto.GetTransactionsByTypeResponse;
import com.mendel.mendel_challenge.dto.PutNewTransactionRequest;
import com.mendel.mendel_challenge.dto.PutNewTransactionResponse;
import com.mendel.mendel_challenge.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction", description = "Transactions API")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PutMapping("/{transactionId}")
    @Operation(
        summary = "Put new transaction",
        description = "Add a new transaction to the system"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Invalid transaction data")
    public ResponseEntity<PutNewTransactionResponse> putNewTransaction(
        @Parameter(description = "Transaction ID", example = "1010", required = true)
        @PathVariable Long transactionId,
        @Valid @RequestBody PutNewTransactionRequest request) {
        PutNewTransactionResponse response = transactionService.putNewTransaction(request, transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types/{type}")
    @Operation(
        summary = "Get transactions by type",
        description = "Returns a list of transaction IDs that match the specified type"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Type not found")
    public ResponseEntity<GetTransactionsByTypeResponse> getTransactionsByType(
        @Parameter(description = "Transaction type", example = "DEBIT", required = true)
        @PathVariable String type) {
        GetTransactionsByTypeResponse response = transactionService.getTransactionsByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sum/{transactionId}")
    @Operation(
        summary = "Get sum of child transactions",
        description = "Returns the sum of all transactions that have the specified transaction as parent"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<GetSumTransactionResponse> getSumByTransactionId(
        @Parameter(description = "Transaction ID", example = "1000", required = true)
        @PathVariable Long transactionId) {
        GetSumTransactionResponse response = transactionService.getSumByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }
}