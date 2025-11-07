package com.mendel.mendel_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorResponse {
    
    @Schema(description = "Error message", example = "Resource not found")
    private String message;
    
    @Schema(description = "HTTP status code", example = "404")
    private int status;
    
    @Schema(description = "Timestamp", example = "2025-11-06T23:30:00")
    private String timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, int status, String timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
