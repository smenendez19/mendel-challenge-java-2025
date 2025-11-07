package com.mendel.mendel_challenge.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class GetTransactionsByTypeResponse {
    
    @Schema(description = "List of transaction IDs", example = "[1001, 1002, 1003]")
    private List<Long> transactionIds;

    public GetTransactionsByTypeResponse() {
    }

    public GetTransactionsByTypeResponse(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }

    public List<Long> getTransactionIds() {
        return transactionIds;
    }

    public void setTransactionIds(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
