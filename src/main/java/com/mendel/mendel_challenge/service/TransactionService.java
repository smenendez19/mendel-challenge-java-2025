package com.mendel.mendel_challenge.service;

import com.mendel.mendel_challenge.dto.GetSumTransactionResponse;
import com.mendel.mendel_challenge.dto.GetTransactionsByTypeResponse;
import com.mendel.mendel_challenge.dto.PutNewTransactionRequest;
import com.mendel.mendel_challenge.dto.PutNewTransactionResponse;

public interface TransactionService {
    PutNewTransactionResponse putNewTransaction(PutNewTransactionRequest request, long transactionId);
    GetTransactionsByTypeResponse getTransactionsByType(String type);
    GetSumTransactionResponse getSumByTransactionId(Long transactionId);
}