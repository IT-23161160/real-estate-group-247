package com.realEstate.repository;

import com.realEstate.model.Transaction;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class TransactionRepository {
    private static final String FILE_PATH = "data/transactions.txt";
    private static final String TEMP_FILE_PATH = "data/transactions_temp.txt";
    private static final String DELIMITER = "\\|"; // for splitting
    private static final String JOIN_DELIMITER = "|"; // for writing

    @PostConstruct
    public void initializeStorage() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists() && !dataDir.mkdirs()) {
                throw new IOException("Failed to create data directory");
            }

            File file = new File(FILE_PATH);
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create transactions file");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize transaction storage: " + e.getMessage(), e);
        }
    }



    public Transaction save(Transaction transaction) {
        if (transaction.getTransactionId() == null) {
            transaction.setTransactionId(generateTransactionId());
        }
        if (transaction.getTime() == null) {
            transaction.setTime(LocalDateTime.now());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(convertToFileLine(transaction));
            return transaction;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save transaction: " + e.getMessage(), e);
        }
    }

    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(convertFromFileLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public Optional<Transaction> findById(String transactionId) {
        return findAll().stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst();
    }

    public List<Transaction> findByUserId(String userId) {
        List<Transaction> results = new ArrayList<>();
        for (Transaction t : findAll()) {
            if (t.getBuyerId().equals(userId) || t.getSellerId().equals(userId)) {
                results.add(t);
            }
        }
        return results;
    }

    public Transaction update(Transaction updated) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File(TEMP_FILE_PATH);
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Transaction existing = convertFromFileLine(line);
                if (existing.getTransactionId().equals(updated.getTransactionId())) {
                    writer.println(convertToFileLine(updated));
                    found = true;
                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update transaction: " + e.getMessage(), e);
        }

        if (!found) {
            throw new IllegalArgumentException("Transaction not found with ID: " + updated.getTransactionId());
        }

        if (!replaceFile(inputFile, tempFile)) {
            throw new RuntimeException("Failed to update transactions file");
        }

        return updated;
    }

    public void deleteById(String transactionId) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File(TEMP_FILE_PATH);
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Transaction t = convertFromFileLine(line);
                if (!t.getTransactionId().equals(transactionId)) {
                    writer.println(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete transaction: " + e.getMessage(), e);
        }

        if (!deleted) {
            throw new IllegalArgumentException("Transaction not found with ID: " + transactionId);
        }

        if (!replaceFile(inputFile, tempFile)) {
            throw new RuntimeException("Failed to update transactions file after deletion");
        }
    }

    private String convertToFileLine(Transaction t) {
        return String.join(JOIN_DELIMITER,
                t.getTransactionId(),
                t.getBuyerId(),
                t.getSellerId(),
                t.getPropertyId(),
                String.valueOf(t.getPrice()),
                t.getCardNumber(),
                t.getCardType(),
                t.getCardExpiryDate(),
                t.getCardCVV(),
                t.getTime().toString()
        );
    }

    private Transaction convertFromFileLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid transaction format: " + line);
        }

        return new Transaction(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                Double.parseDouble(parts[4]),
                parts[5],
                parts[6],
                parts[7],
                parts[8],
                LocalDateTime.parse(parts[9])
        );
    }

    private boolean replaceFile(File original, File temp) {
        return original.delete() && temp.renameTo(original);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8);
    }
}