package com.ejada.accounts.Repos;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejada.accounts.Models.AccountModel;

import java.util.List;


@Repository
public interface AccountRepo extends JpaRepository<AccountModel, UUID> {


    List<AccountModel> findByUserId(UUID user_id);

}
