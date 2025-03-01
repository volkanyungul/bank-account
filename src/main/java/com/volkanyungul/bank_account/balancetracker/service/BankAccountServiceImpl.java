package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.balancetracker.repository.BalanceRepository;
import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService, ApplicationListener<TransactionCreatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    private final BalanceRepository balanceRepository;

    @Override
    public void processTransaction(Transaction transaction) {
        logger.info("Transaction Received: {}" , transaction);
        balanceRepository.add(transaction.amount());
    }

    @Override
    public double retrieveBalance() {
        return balanceRepository.retrieve().doubleValue();
    }

    @Override
    public void onApplicationEvent(TransactionCreatedEvent event) {
        this.processTransaction(event.getTransaction());
    }
}
