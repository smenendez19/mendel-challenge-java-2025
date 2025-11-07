# Mendel Challenge Java 2025

## Description

Mendel Challenge REST API in Java.

## Quick Start with Docker

* Requires Docker installed and docker-compose

```bash
docker-compose up --build
```

The application will be available at: **<http://localhost:8080>**

## API Documentation

Once the application is running, access the interactive API documentation:

**Swagger UI**: <http://localhost:8080/swagger-ui.html>

## API Endpoints

### PUT /transactions/{transaction_id}

Create a new transaction with a specific ID.

**Request Body:**

```json
{
  "amount": 99.99,
  "type": "DEBIT",
  "parentId": 1000
}
```

**Response (200 OK):**

```json
{
  "status": "ok"
}
```

**Error (400 BAD REQUEST):** If transaction ID already exists

---

### GET /transactions/types/{type}

Get all transaction IDs of a specific type.

**Example:** `GET /transactions/types/DEBIT`

**Response (200 OK):**

```json
{
  "transactionIds": [1001, 1002, 1003]
}
```

---

### GET /transactions/sum/{transaction_id}

Get the sum of all transactions that have the specified transaction as parent.

**Example:** `GET /transactions/sum/1000`

**Response (200 OK):**

```json
{
  "sum": 299.97
}
```

## Features

* Spring Boot 3.5.7
* In-Memory Storage (ConcurrentHashMap)
* RESTful API
* OpenAPI/Swagger Documentation
* Docker Support
* Exception Handling
* Request Validation
* Logging

## Architecture

This project follows a **Layered Architecture**:

``` text
src/main/java/com/mendel/mendel_challenge/
├── controller/          # REST API endpoints
├── service/            # Business logic
├── repository/         # Data access
├── model/              # Domain entities
├── dto/                # Data Transfer Objects
├── exception/          # Custom exceptions and handlers
└── config/             # Application configuration
```
