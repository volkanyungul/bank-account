package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.dto.Submission;
import com.volkanyungul.bank_account.auditsystem.service.submitter.AuditSubmitter;
import com.volkanyungul.bank_account.events.AuditReadyEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

@RequiredArgsConstructor
@Component
@Slf4j
public class BatchAuditProcessor implements AuditProcessor {

    private final AuditSystemProperties auditSystemProperties;

    private final AuditSubmitter auditSubmitter;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Async
    @Override
    public void process(PriorityQueue<Transaction> auditTransactionsPriorityQueue) {
        var batches = splitAuditTransactionsIntoBatches(new PriorityQueue<>(auditTransactionsPriorityQueue));
        publishAuditReadyEvent(batches, auditTransactionsPriorityQueue);
        submitAudit(batches);
    }

    private List<Batch> splitAuditTransactionsIntoBatches(PriorityQueue<Transaction> auditTransactionsPriorityQueue) {
        List<Batch> batches = new ArrayList<>();

        while(!auditTransactionsPriorityQueue.isEmpty()) {
            var transaction = auditTransactionsPriorityQueue.poll();

            findAvailableBatch(batches, transaction).ifPresentOrElse(batch -> batch.addTransaction(transaction), () -> {
                var batch = new Batch(auditSystemProperties.getTotalValueOfAllTransactionsThreshold());
                batch.addTransaction(transaction);
                batches.add(batch);
            });
        }
        return batches;
    }

    private Optional<Batch> findAvailableBatch(List<Batch> batches, Transaction transaction) {
        return batches.stream()
                .filter(batch -> batch.hasSpaceFor(transaction))
                .findFirst();
    }

    private void publishAuditReadyEvent(List<Batch> batches, PriorityQueue<Transaction> transactions) {
        // This is implemented for integration test to verify randomly generated transactions mapped correctly into Audit or not
        applicationEventPublisher.publishEvent(new AuditReadyEvent(this, batches, transactions));
    }

    private void submitAudit(List<Batch> batches) {
        auditSubmitter.submit(AuditSubmission.builder().submission(Submission.builder().batches(batches).build()).build());
    }
}
