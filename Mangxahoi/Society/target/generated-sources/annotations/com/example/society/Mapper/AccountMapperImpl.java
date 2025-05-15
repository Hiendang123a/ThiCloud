package com.example.society.Mapper;

import com.example.society.DTO.Request.AccountDTO;
import com.example.society.DTO.Response.UpdateAccountResponse;
import com.example.society.Entity.Account;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-03T21:00:24+0700",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public Account toAccount(AccountDTO accountDTO) {
        if ( accountDTO == null ) {
            return null;
        }

        Account account = new Account();

        account.setUsername( accountDTO.getUsername() );
        account.setPassword( accountDTO.getPassword() );

        return account;
    }

    @Override
    public UpdateAccountResponse toUpdateAccountResponse(Account account) {
        if ( account == null ) {
            return null;
        }

        UpdateAccountResponse updateAccountResponse = new UpdateAccountResponse();

        updateAccountResponse.setUsername( account.getUsername() );
        updateAccountResponse.setLastLoginAt( account.getLastLoginAt() );
        updateAccountResponse.setIsPrivate( account.getIsPrivate() );

        return updateAccountResponse;
    }
}
