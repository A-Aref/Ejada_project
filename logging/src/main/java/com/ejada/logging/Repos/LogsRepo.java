package com.ejada.logging.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejada.logging.Models.LogsModel;

@Repository
public interface LogsRepo extends JpaRepository<LogsModel, Long> {

}
