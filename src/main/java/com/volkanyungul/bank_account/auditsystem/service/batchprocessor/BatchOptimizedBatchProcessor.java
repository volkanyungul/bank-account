package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.service.submitter.AuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

@Component
@Slf4j
@ConditionalOnProperty(name = "audit-system.performance.batchAuditProcessorAlgorithm", havingValue = "BatchOptimized", matchIfMissing = true)
public class BatchOptimizedBatchProcessor extends AbstractBatchProcessor {

    public BatchOptimizedBatchProcessor(AuditSubmitter auditSubmitter, ApplicationEventPublisher applicationEventPublisher, AuditSystemProperties auditSystemProperties) {
        super(auditSubmitter, applicationEventPublisher, auditSystemProperties);
    }

    public List<Batch> splitAuditTransactionsIntoBatches(PriorityQueue<Transaction> auditTransactionsPriorityQueue) {
        PriorityQueue<Batch> batches = new PriorityQueue<>(Comparator.comparing(Batch::getRemainingLimit, Comparator.reverseOrder()));

        while(!auditTransactionsPriorityQueue.isEmpty()) {
            var transaction = auditTransactionsPriorityQueue.poll();

            findAvailableBatch(batches, transaction).ifPresentOrElse(batch -> batch.addTransaction(transaction), () -> {
                var batch = new Batch(auditSystemProperties.getTotalValueOfAllTransactionsThreshold());
                batch.addTransaction(transaction);
                batches.add(batch);
            });
        }
        return batches.stream().toList();
    }

    private Optional<Batch> findAvailableBatch(PriorityQueue<Batch> batches, Transaction transaction) {
        return batches.stream()
                .filter(batch -> batch.hasSpaceFor(transaction))
                .findFirst();
    }

}
