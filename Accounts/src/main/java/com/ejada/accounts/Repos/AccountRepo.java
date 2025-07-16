package com.ejada.accounts.Repos;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ejada.accounts.models.AccountModel;
import java.util.List;


@Repository
public interface AccountRepo extends JpaRepository<AccountModel, UUID> {


    List<AccountModel> findByUser_id(UUID user_id);

}
