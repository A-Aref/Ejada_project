package com.ejada.transactions.Repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejada.transactions.Models.TransactionModel;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionModel, UUID> {

    public List<TransactionModel> findByFrom_account_id(UUID fromAccountId);
    public List<TransactionModel> findByTo_account_id(UUID toAccountId);
}
