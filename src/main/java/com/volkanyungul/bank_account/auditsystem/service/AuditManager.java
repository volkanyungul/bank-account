package com.volkanyungul.bank_account.auditsystem.service;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.service.batchprocessor.AuditProcessor;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class AuditManager {

    private static final Comparator<Transaction> TRANSACTION_COMPARATOR =
            Comparator.comparing(transaction -> transaction.amount().abs(), Comparator.reverseOrder());

    private PriorityQueue<Transaction> transactionPriorityQueue = new PriorityQueue<>(TRANSACTION_COMPARATOR);

    private final AuditProcessor auditProcessor;

    private final AuditSystemProperties auditSystemProperties;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    public void receiveTransaction(Transaction transaction) {
        reentrantLock.lock();
        try {
            transactionPriorityQueue.add(transaction);

            if (transactionPriorityQueue.size() == auditSystemProperties.getTransactionCountThreshold()) {
                sendAuditsAndResetQueue();
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    private void sendAuditsAndResetQueue() {
        auditProcessor.process(transactionPriorityQueue);
        transactionPriorityQueue = new PriorityQueue<>(TRANSACTION_COMPARATOR);
    }
}
