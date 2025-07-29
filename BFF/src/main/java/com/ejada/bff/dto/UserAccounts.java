package com.ejada.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class UserAccounts {
    private List<Account> accounts;
    public UserAccounts(){
        this.accounts=new ArrayList<Account>();
    }
}
