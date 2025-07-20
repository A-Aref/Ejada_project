package com.ejada.transactions.Repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejada.transactions.Models.TransactionModel;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionModel, UUID> {

    
    // Find all transactions for a specific account (as sender or receiver) ordered by date descending
    public List<TransactionModel> findByFromAccountIdOrToAccountId(UUID fromAccountId, UUID toAccountId);
    
    // Find the newest transaction for a specific account (as sender or receiver)
    public TransactionModel findFirstByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(UUID fromAccountId, UUID toAccountId);
}
