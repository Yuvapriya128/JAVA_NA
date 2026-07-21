package org.northernarc.week5_assess.service;

import org.northernarc.week5_assess.dto.AccountDto;
import org.northernarc.week5_assess.dto.TransferRequestDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

	Object createAccount(AccountDto accountDto);

	Object getAccountById(Long id);

	List<Object> getAllAccounts();

	Object updateAccount(Long id, AccountDto accountDto);

	void deleteAccount(Long id);

	Object deposit(String accountNumber, BigDecimal amount);

	Object withdraw(String accountNumber, BigDecimal amount);

	Object transfer(TransferRequestDto transferRequestDto);
}

