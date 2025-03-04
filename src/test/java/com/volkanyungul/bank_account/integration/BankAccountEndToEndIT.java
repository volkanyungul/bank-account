package com.volkanyungul.bank_account.integration;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Import(EndToEndITConfig.class)
public class BankAccountEndToEndIT {

    @Autowired
    private AuditSubmissionITEventListener auditSubmissionITEventListener;

    @Autowired
    private AuditSystemProperties auditSystemProperties;

    @Test
    void generateTransactionsUntilFirstSubmissionBatchOfAuditIsPrepared() {
        // given
        // when
        List<Batch> batches = auditSubmissionITEventListener.getBatches();
        long transactionCountInAllBatches = batches.stream().mapToLong(Batch::getCountOfTransactions).sum();
        PriorityQueue<Transaction> transactions = auditSubmissionITEventListener.getTransactions();

        // then
        assertEquals(auditSystemProperties.getTransactionCountThreshold(), transactionCountInAllBatches);
        assertEquals(transactions.size(), transactionCountInAllBatches);

        verifyTotalValueOfTransactionInEachBatchIsSmallerThanThreshold(batches, auditSystemProperties.getTotalValueOfAllTransactionsThreshold());

        verifyThatTransactionCountInAllBatchesMatchesTransactionAmountTotal(batches, transactions);
    }

    protected void verifyThatTransactionCountInAllBatchesMatchesTransactionAmountTotal(List<Batch> batches, PriorityQueue<Transaction> transactions) {
        assertEquals(batches.stream().map(Batch::getTotalValueOfAllTransactions).reduce(BigDecimal.ZERO, BigDecimal::add),
                transactions.stream().map(transaction -> transaction.amount().abs()).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    protected void verifyTotalValueOfTransactionInEachBatchIsSmallerThanThreshold(List<Batch> batches, BigDecimal totalValueOfTransactionsThreshold) {
        assertTrue(batches.stream().anyMatch(batch -> batch.getTotalValueOfAllTransactions().compareTo(totalValueOfTransactionsThreshold) <= 0));
    }
}