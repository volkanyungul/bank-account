package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionCreatedEventHandler implements ApplicationListener<TransactionCreatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionCreatedEventHandler.class);

    private final BankAccountService bankAccountService;

    @Override
    public void onApplicationEvent(TransactionCreatedEvent event) {
        logger.info("Transaction Received: {}" , event.getTransaction());
        bankAccountService.processTransaction(event.getTransaction());
    }
}
