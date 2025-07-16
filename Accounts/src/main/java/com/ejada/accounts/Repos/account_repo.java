package com.ejada.accounts.Repos;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ejada.accounts.models.account_model;

@Repository
public interface account_repo extends JpaRepository<account_model, UUID> {

}
