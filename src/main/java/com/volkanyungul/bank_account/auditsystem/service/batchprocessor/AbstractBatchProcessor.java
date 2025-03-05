package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.events.AuditReadyEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
abstract class AbstractBatchProcessor implements BatchProcessor {

    protected final ApplicationEventPublisher applicationEventPublisher;

    protected final AuditSystemProperties auditSystemProperties;

    abstract List<Batch> splitAuditTransactionsIntoBatches(PriorityQueue<Transaction> auditTransactionsPriorityQueue);

    @Async
    @Override
    public CompletableFuture<List<Batch>> process(PriorityQueue<Transaction> auditTransactionsPriorityQueue) {
        var batches = splitAuditTransactionsIntoBatches(new PriorityQueue<>(auditTransactionsPriorityQueue));
        publishAuditReadyEvent(batches, auditTransactionsPriorityQueue);
        return CompletableFuture.completedFuture(batches);
    }

    private void publishAuditReadyEvent(List<Batch> batches, PriorityQueue<Transaction> transactions) {
        // This is implemented for integration test to verify randomly generated transactions mapped correctly into Audit or not
        applicationEventPublisher.publishEvent(new AuditReadyEvent(this, batches, transactions));
    }
}
