package com.ejada.transactions.Repos;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejada.transactions.Models.transaction_model;

public interface transaction_repo extends JpaRepository<transaction_model, UUID> {

    
}
