package com.mendel.mendel_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PutNewTransactionResponse {
    
    @Schema(description = "Status of the operation", example = "ok")
    private String status;

    public PutNewTransactionResponse() {
    }

    public PutNewTransactionResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}