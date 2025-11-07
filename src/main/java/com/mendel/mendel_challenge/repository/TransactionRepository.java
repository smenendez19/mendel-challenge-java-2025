package com.mendel.mendel_challenge.repository;

import java.util.List;
import java.util.Optional;

import com.mendel.mendel_challenge.model.Transaction;

public interface TransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findByType(String type);
    List<Transaction> findByParentId(Long parentId);
    List<Transaction> findAll();
    void deleteAll();
}