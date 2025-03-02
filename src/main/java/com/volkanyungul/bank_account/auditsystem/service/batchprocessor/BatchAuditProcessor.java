package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.dto.Submission;
import com.volkanyungul.bank_account.auditsystem.service.submission.AuditSubmitter;
import com.volkanyungul.bank_account.auditsystem.service.submission.ConsoleAuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void process(PriorityQueue<Transaction> auditTransactionsPriorityQueue) {
        List<Batch> batchList = splitAuditTransactionsIntoBatches(auditTransactionsPriorityQueue);
        submitBatch(batchList);
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

    private void submitBatch(List<Batch> batches) {
        auditSubmitter.submit(AuditSubmission.builder().submission(Submission.builder().batches(batches).build()).build());
    }
}
