package com.ejada.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor

public class AccountTransactions {
    public AccountTransactions(){
        this.transactions=new ArrayList<Transaction>();
    }
    private List<Transaction> transactions;
}
