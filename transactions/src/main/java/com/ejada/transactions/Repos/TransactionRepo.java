package com.ejada.transactions.Repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejada.transactions.Models.TransactionModel;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionModel, UUID> {

    public List<TransactionModel> findByFromAccountIdAndToAccountId(UUID fromAccountId, UUID toAccountId);
    
    // Find by both account IDs ordered by created_at descending, get first (most recent)
    public TransactionModel findFirstByFromAccountIdAndToAccountIdOrderByCreatedAtDesc(UUID fromAccountId, UUID toAccountId);
}
