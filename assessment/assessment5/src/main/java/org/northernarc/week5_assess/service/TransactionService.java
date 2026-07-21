package org.northernarc.week5_assess.service;

import java.util.List;

public interface TransactionService {

	Object getTransactionById(Long id);

	List<Object> getAllTransactions();

	List<Object> getTransactionsByAccountId(Long id);

	List<Object> getTransactionsByAccount(String accountNumber);
}

