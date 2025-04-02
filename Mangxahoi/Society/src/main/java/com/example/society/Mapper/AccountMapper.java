package com.example.society.Mapper;

import com.example.society.DTO.Request.AccountDTO;
import com.example.society.Entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccount(AccountDTO accountDTO);
}