package com.volkanyungul.bank_account.auditsystem.service;

import com.volkanyungul.bank_account.events.TransactionProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProcessedEventHandler implements ApplicationListener<TransactionProcessedEvent> {

    private final AuditManager auditManager;

    @Override
    public void onApplicationEvent(TransactionProcessedEvent event) {
        log.info("Transaction Processed: {}" , event.getTransaction());
        auditManager.receiveTransaction(event.getTransaction());
    }
}
