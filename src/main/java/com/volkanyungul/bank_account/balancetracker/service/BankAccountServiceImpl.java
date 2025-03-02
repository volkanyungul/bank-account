package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.balancetracker.repository.BalanceRepository;
import com.volkanyungul.bank_account.events.TransactionProcessedEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BalanceRepository balanceRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void processTransaction(Transaction transaction) {
        balanceRepository.add(transaction.amount());
        applicationEventPublisher.publishEvent(new TransactionProcessedEvent(this, transaction));
    }

    @Override
    public double retrieveBalance() {
        return balanceRepository.retrieve().doubleValue();
    }
}
