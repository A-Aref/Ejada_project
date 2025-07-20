package com.ejada.transactions.Services;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ejada.transactions.Models.TransactionStatus;
import com.ejada.transactions.Models.TransactionModel;
import com.ejada.transactions.Repos.TransactionRepo;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;
    
    public TransactionModel getTransaction(UUID transactionId) {
        return transactionRepo.findById(transactionId).orElse(null);
    }

    public List<HashMap<String,Object>> getTransactions(UUID accountId) {

        List<TransactionModel> transactions = transactionRepo.findByFromAccountId(accountId);
        transactions.addAll(transactionRepo.findByToAccountId(accountId));


        List<HashMap<String, Object>> result = transactions.stream().map(transaction -> {
            
            final String desc = transaction.getDescription() != null ? transaction.getDescription() : "";
            UUID account = transaction.getFromAccountId() != accountId ? transaction.getFromAccountId() : transaction.getToAccountId();
            Double amount = transaction.getFromAccountId() != accountId ? -transaction.getAmount() : transaction.getAmount();
            
            HashMap<String, Object> map = new HashMap<>();
            map.put("transactionId", transaction.getId());
            map.put("accountId", account);
            map.put("amount", amount);
            map.put("description", desc);
            map.put("timestamp", transaction.getCreatedAt().toString());
            return map;
        }).toList();
        return result;
    }
    
    public TransactionModel initiateTransaction(HashMap<String,Object> transaction) {
        Double amount = (Double) transaction.get("amount");
        if (amount <= 0) {
            return null;
        }
        TransactionModel newTransaction = new TransactionModel();
        newTransaction.setFromAccountId(UUID.fromString((String) transaction.get("fromAccountId")));
        newTransaction.setToAccountId(UUID.fromString((String) transaction.get("toAccountId")));
        newTransaction.setAmount(amount);
        newTransaction.setStatus(TransactionStatus.INITIATED);
        newTransaction.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        if (transaction.containsKey("description")) {
            newTransaction.setDescription((String) transaction.get("description"));
        }
        return transactionRepo.save(newTransaction);
    }

    public TransactionModel excuteTransaction(UUID transactionId) {
        return transactionRepo.findById(transactionId).map(transaction -> {
            transaction.setStatus(TransactionStatus.SUCCESS);
            return transactionRepo.save(transaction);
        }).orElse(null);
    }

    public TransactionModel cancelTransaction(UUID transactionId) {
        return transactionRepo.findById(transactionId).map(transaction -> {
            transaction.setStatus(TransactionStatus.FAILED);
            return transactionRepo.save(transaction);
        }).orElse(null);
    }

}


