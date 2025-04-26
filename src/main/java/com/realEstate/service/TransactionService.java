package com.realEstate.service;

import com.realEstate.model.Transaction;
import com.realEstate.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    public TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction addTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    public Optional<Transaction> getTransaction(String id) {
        return repository.findById(id);
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public List<Transaction> getUserTransactions(String userId) {
        return repository.findByUserId(userId);
    }

    public Transaction updateTransaction(Transaction transaction) {
        return repository.update(transaction);
    }

    public void deleteTransaction(String id) {
        repository.deleteById(id);
    }
}