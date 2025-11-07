package com.mendel.mendel_challenge.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mendel.mendel_challenge.dto.GetSumTransactionResponse;
import com.mendel.mendel_challenge.dto.GetTransactionsByTypeResponse;
import com.mendel.mendel_challenge.dto.PutNewTransactionRequest;
import com.mendel.mendel_challenge.dto.PutNewTransactionResponse;
import com.mendel.mendel_challenge.exception.ResourceAlreadyExistsException;
import com.mendel.mendel_challenge.exception.ResourceNotFoundException;
import com.mendel.mendel_challenge.model.Transaction;
import com.mendel.mendel_challenge.repository.TransactionRepository;


@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public PutNewTransactionResponse putNewTransaction(PutNewTransactionRequest request, long transactionId) {
        logger.info("Processing new transaction with ID: {}", transactionId);

        if (transactionRepository.findById(transactionId).isPresent()) {
            logger.warn("Transaction with ID {} already exists", transactionId);
            throw new ResourceAlreadyExistsException("Transaction", transactionId);
        }

        if (request.getParentId() != null) {
            if (transactionRepository.findById(request.getParentId()).isEmpty()) {
                logger.warn("Parent transaction with ID {} not found", request.getParentId());
                throw new ResourceNotFoundException("Parent Transaction", request.getParentId());
            }
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setParentId(request.getParentId());
        
        transactionRepository.save(transaction);
        logger.info("Transaction created successfully: id={}, type={}, amount={}, parentId={}", 
            transactionId, request.getType(), request.getAmount(), request.getParentId());

        return new PutNewTransactionResponse("ok");
    }

    @Override
    public GetTransactionsByTypeResponse getTransactionsByType(String type) {
        logger.info("Getting transactions by type: {}", type);

        List<Transaction> transactions = transactionRepository.findByType(type);
        
        List<Long> transactionIds = transactions.stream()
                .map(Transaction::getTransactionId)
                .collect(Collectors.toList());

        return new GetTransactionsByTypeResponse(transactionIds);
    }

    @Override
    public GetSumTransactionResponse getSumByTransactionId(Long transactionId) {
        logger.info("Getting sum for transaction ID: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> {
                logger.warn("Transaction with ID {} not found", transactionId);
                return new ResourceNotFoundException("Transaction", transactionId);
            });

        Double sum = transaction.getAmount() + calculateRecursiveSum(transactionId);

        return new GetSumTransactionResponse(sum);
    }

    private Double calculateRecursiveSum(Long parentId) {
        List<Transaction> children = transactionRepository.findByParentId(parentId);
        double sum = 0.0;
        
        for (Transaction child : children) {
            sum += child.getAmount();
            sum += calculateRecursiveSum(child.getTransactionId());
        }
        
        return sum;
    }
}

