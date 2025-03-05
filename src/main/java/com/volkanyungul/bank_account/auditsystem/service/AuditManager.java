package com.volkanyungul.bank_account.auditsystem.service;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.dto.Submission;
import com.volkanyungul.bank_account.auditsystem.service.batchprocessor.BatchProcessor;
import com.volkanyungul.bank_account.auditsystem.service.submitter.AuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditManager {

    private static final Comparator<Transaction> TRANSACTION_COMPARATOR =
            Comparator.comparing(transaction -> transaction.amount().abs(), Comparator.reverseOrder());

    private PriorityQueue<Transaction> transactionPriorityQueue = new PriorityQueue<>(TRANSACTION_COMPARATOR);

    private final BatchProcessor batchProcessor;

    protected final AuditSubmitter auditSubmitter;

    private final AuditSystemProperties auditSystemProperties;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    public void receiveTransaction(Transaction transaction) {
        reentrantLock.lock();
        try {
            transactionPriorityQueue.add(transaction);

            if (transactionPriorityQueue.size() == auditSystemProperties.getTransactionCountThreshold()) {
                prepareBatchAndResetQueue();
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    private void prepareBatchAndResetQueue() {
        batchProcessor.process(transactionPriorityQueue).thenAccept(this::submitAudit);
        transactionPriorityQueue = new PriorityQueue<>(TRANSACTION_COMPARATOR);
    }

    private void submitAudit(List<Batch> batches) {
        auditSubmitter.submit(AuditSubmission.builder().submission(Submission.builder().batches(batches).build()).build());
    }
}
