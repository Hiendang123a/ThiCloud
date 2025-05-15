package com.example.society.Mapper;

import com.example.society.DTO.Request.AccountDTO;
import com.example.society.DTO.Response.UpdateAccountResponse;
import com.example.society.Entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "accountID", ignore = true)
    @Mapping(target = "userID", ignore = true)
    @Mapping(target = "failedAttempts", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isPrivate", ignore = true)
    Account toAccount(AccountDTO accountDTO);
    UpdateAccountResponse toUpdateAccountResponse(Account account);
}