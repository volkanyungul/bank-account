package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.service.submitter.AuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Component
@Slf4j
@ConditionalOnProperty(name = "audit-system.performance.batchAuditProcessorAlgorithm", havingValue = "TimeOptimized", matchIfMissing = true)
public class TimeOptimizedAuditProcessor extends AbstractAuditProcessor {

    public TimeOptimizedAuditProcessor(AuditSubmitter auditSubmitter, ApplicationEventPublisher applicationEventPublisher, AuditSystemProperties auditSystemProperties) {
        super(auditSubmitter, applicationEventPublisher, auditSystemProperties);
    }

    public List<Batch> splitAuditTransactionsIntoBatches(PriorityQueue<Transaction> auditTransactionsPriorityQueue) {
        List<Batch> batches = new ArrayList<>();
        Batch currentBatch = new Batch(auditSystemProperties.getTotalValueOfAllTransactionsThreshold());

        while(!auditTransactionsPriorityQueue.isEmpty()) {
            var transaction = auditTransactionsPriorityQueue.poll();
            if(!currentBatch.hasSpaceFor(transaction)) {
                batches.add(currentBatch);
                currentBatch = new Batch(auditSystemProperties.getTotalValueOfAllTransactionsThreshold());
            }
            currentBatch.addTransaction(transaction);
        }
        if(currentBatch.getTotalValueOfAllTransactions().compareTo(BigDecimal.ZERO) > 0) {
            batches.add(currentBatch);
        }
        return batches;
    }
}
