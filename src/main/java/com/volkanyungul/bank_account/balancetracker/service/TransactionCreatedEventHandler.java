package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCreatedEventHandler implements ApplicationListener<TransactionCreatedEvent> {

    private final BankAccountService bankAccountService;

    @Override
    public void onApplicationEvent(TransactionCreatedEvent event) {
        log.debug("Transaction Created: {}" , event.getTransaction());
        bankAccountService.processTransaction(event.getTransaction());
    }
}
