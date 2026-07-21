package org.northernarc.week5_assess.service.impl;

import org.northernarc.week5_assess.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Override
    public Object getTransactionById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Object> getAllTransactions() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Object> getTransactionsByAccountId(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Object> getTransactionsByAccount(String accountNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

