package com.volkanyungul.bank_account.integration.performance;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.integration.AuditSubmissionITEventListener;
import com.volkanyungul.bank_account.integration.BankAccountEndToEndITConfig;
import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Import(BankAccountEndToEndITConfig.class)
class PerformanceIT {

    public static final String PERFORMANCE_CONSOLE_LOG_FORMAT = """
                --------------------------------------------------------------
                --------------------------------------------------------------
                Config
                --------------------------------------------------------------
                Thread Count: {}
                Thread Running Period: {} (second)
                Credit Transaction Count: {} (per second)
                Debit Transaction Count: {} (per second)
                Transaction Count Threshold: {}
                Transaction Total Value Of All Transactions Threshold: {}
                --------------------------------------------------------------
                TRANSACTION OVERVIEW
                --------------------------------------------------------------
                Processed Transaction Count: {}
                Transaction Total Amount: {}
                --------------------------------------------------------------
                BATCH OVERVIEW
                --------------------------------------------------------------
                Batch Count: {}
                Transaction Total Count In All Batches: {}
                Batch Total Amount: {}
                --------------------------------------------------------------
                PERFORMANCE RESULT
                --------------------------------------------------------------
                Produced Batch Count: {}
                Total Execution Time: {}
                --------------------------------------------------------------
                --------------------------------------------------------------
                """;

    @Autowired
    private AuditSubmissionITEventListener auditSubmissionITEventListener;

    @Autowired
    private ProducerProperties producerProperties;

    @Autowired
    private AuditSystemProperties auditSystemProperties;

    private static long startTime;

    @BeforeAll
    static void beforeAll() {
        startTime = System.nanoTime();
    }

    @AfterEach
    void logPerformanceResults() {
        var batches = auditSubmissionITEventListener.getBatches();
        var transactions = auditSubmissionITEventListener.getTransactions();
        var transactionTypeConfigMap = producerProperties.getTransactionTypeConfigMap();

        long duration = System.nanoTime() - startTime;
        long transactionCountInAllBatches = batches.stream().mapToLong(Batch::getCountOfTransactions).sum();
        BigDecimal transactionTotalAmount = transactions.stream().map(transaction -> transaction.amount().abs()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal batchTotalAmount = batches.stream().map(Batch::getTotalValueOfAllTransactions).reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info(PERFORMANCE_CONSOLE_LOG_FORMAT, transactionTypeConfigMap.size(), producerProperties.getScheduler().getPeriodInSeconds(),
                transactionTypeConfigMap.get(TransactionType.CREDIT).getTransactionCountPerSecond(), transactionTypeConfigMap.get(TransactionType.DEBIT).getTransactionCountPerSecond(),
                auditSystemProperties.getTransactionCountThreshold(), auditSystemProperties.getTotalValueOfAllTransactionsThreshold(),
                transactions.size(), transactionTotalAmount, batches.size(), transactionCountInAllBatches, batchTotalAmount, batches.size(), duration / 1_000_000);
    }

    @Test
    void generateTransactionsUntilFirstSubmissionBatchOfAuditIsPrepared() {
        assertNotNull(auditSubmissionITEventListener.getBatches());
    }
}
