package org.northernarc.week5_assess.service.impl;

import org.northernarc.week5_assess.dto.AccountDto;
import org.northernarc.week5_assess.dto.TransferRequestDto;
import org.northernarc.week5_assess.service.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Override
    public Object createAccount(AccountDto accountDto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object getAccountById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Object> getAllAccounts() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object updateAccount(Long id, AccountDto accountDto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAccount(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object deposit(String accountNumber, BigDecimal amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object withdraw(String accountNumber, BigDecimal amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object transfer(TransferRequestDto transferRequestDto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

