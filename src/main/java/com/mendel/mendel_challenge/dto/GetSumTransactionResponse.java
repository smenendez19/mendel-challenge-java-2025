package com.mendel.mendel_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class GetSumTransactionResponse {
    
    @Schema(description = "Sum of all child transactions amounts", example = "299.97")
    private Double sum;

    public GetSumTransactionResponse() {
    }

    public GetSumTransactionResponse(Double sum) {
        this.sum = sum;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
