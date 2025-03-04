package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.service.submitter.AuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

@Component
@Slf4j
@ConditionalOnProperty(name = "audit-system.performance.batchAuditProcessorAlgorithm", havingValue = "BatchOptimized", matchIfMissing = true)
public class BatchOptimizedAuditProcessor extends AbstractAuditProcessor {

    public BatchOptimizedAuditProcessor(AuditSubmitter auditSubmitter, ApplicationEventPublisher applicationEventPublisher, AuditSystemProperties auditSystemProperties) {
        super(auditSubmitter, applicationEventPublisher, auditSystemProperties);
    }

    public List<Batch> splitAuditTransactionsIntoBatches(PriorityQueue<Transaction> auditTransactionsPriorityQueue) {
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

}
