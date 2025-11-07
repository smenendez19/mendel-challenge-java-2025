package com.mendel.mendel_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PutNewTransactionRequest {
    
    @Schema(description = "Transaction amount", example = "99.99", required = true)
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @Schema(description = "Transaction type", example = "DEBIT", required = true)
    @NotBlank(message = "Type is required")
    private String type;
    
    @Schema(description = "Parent transaction ID", example = "1000", required = false)
    private Long parentId;

    public PutNewTransactionRequest() {
    }

    public PutNewTransactionRequest(Double amount, String type, Long parentId) {
        this.amount = amount;
        this.type = type;
        this.parentId = parentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}