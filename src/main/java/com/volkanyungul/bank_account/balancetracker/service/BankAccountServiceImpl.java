package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.balancetracker.repository.BalanceRepository;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BalanceRepository balanceRepository;

    @Override
    public void processTransaction(Transaction transaction) {
        balanceRepository.add(transaction.amount());
    }

    @Override
    public double retrieveBalance() {
        return balanceRepository.retrieve().doubleValue();
    }
}
