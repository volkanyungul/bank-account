package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.service.submitter.ConsoleLoggingAuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static com.volkanyungul.bank_account.producer.dto.TransactionType.DEBIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeOptimizedBatchProcessorTest {

    @Mock
    private AuditSystemProperties mockAuditSystemProperties;

    @Mock
    private ConsoleLoggingAuditSubmitter mockConsoleLoggingAuditSubmitter;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    /*
        Amounts in the transactions are like [9 , 8 , 8 , 5, 3, 2, 2, 1, 1], and totalValueOfAllTransactionsThreshold is 10.
        It should create batches including below amounts
        Batch 1 -> [9]
        Batch 2 -> [8]
        Batch 3 -> [8]
        Batch 4 -> [5,3,2]
        Batch 5 -> [2,1,1]
        */
    @Test
    void shouldSplitTheTransactionsInTheQueueIntoBatches() {
        // given
        PriorityQueue<Transaction> auditTransactionsPriorityQueue =
                new PriorityQueue<>(Comparator.comparing(transaction -> transaction.amount().abs(), Comparator.reverseOrder()));
        auditTransactionsPriorityQueue.addAll(createMockTransactions());

        var batchAuditNonRevisitingBatchesProcessor = new TimeOptimizedBatchProcessor(mockConsoleLoggingAuditSubmitter, mockApplicationEventPublisher, mockAuditSystemProperties);
        when(mockAuditSystemProperties.getTotalValueOfAllTransactionsThreshold()).thenReturn(new BigDecimal("10"));
        // when
        batchAuditNonRevisitingBatchesProcessor.process(auditTransactionsPriorityQueue);
        // then
        ArgumentCaptor<AuditSubmission> auditSubmissionArgumentCaptor = ArgumentCaptor.forClass(AuditSubmission.class);
        verify(mockConsoleLoggingAuditSubmitter, times(1)).submit(auditSubmissionArgumentCaptor.capture());
        List<Batch> batches = auditSubmissionArgumentCaptor.getValue().submission().batches();
        validateBatches(batches);
    }

    private List<Transaction> createMockTransactions() {
        return List.of(Transaction.builder().id("123").transactionType(CREDIT).amount(new BigDecimal("9")).build(), Transaction.builder().id("456").transactionType(DEBIT).amount(new BigDecimal("-8")).build(),
                Transaction.builder().id("789").transactionType(CREDIT).amount(new BigDecimal("8")).build(), Transaction.builder().id("321").transactionType(DEBIT).amount(new BigDecimal("-5")).build(),
                Transaction.builder().id("654").transactionType(CREDIT).amount(new BigDecimal("3")).build(), Transaction.builder().id("987").transactionType(DEBIT).amount(new BigDecimal("-2")).build(),
                Transaction.builder().id("135").transactionType(CREDIT).amount(new BigDecimal("2")).build(), Transaction.builder().id("357").transactionType(DEBIT).amount(new BigDecimal("-1")).build(),
                Transaction.builder().id("579").transactionType(CREDIT).amount(new BigDecimal("1")).build());
    }

    private static void validateBatches(List<Batch> batches) {
        // validate transaction counts
        assertEquals(5L, batches.size());
        assertEquals(1L, batches.get(0).getCountOfTransactions());
        assertEquals(1L, batches.get(1).getCountOfTransactions());
        assertEquals(1L, batches.get(2).getCountOfTransactions());
        assertEquals(3L, batches.get(3).getCountOfTransactions());
        assertEquals(3L, batches.get(4).getCountOfTransactions());
        // validate total value of all transactions in a batch
        assertEquals(0, new BigDecimal("9").compareTo(batches.get(0).getTotalValueOfAllTransactions()));
        assertEquals(0, new BigDecimal("8").compareTo(batches.get(1).getTotalValueOfAllTransactions()));
        assertEquals(0, new BigDecimal("8").compareTo(batches.get(2).getTotalValueOfAllTransactions()));
        assertEquals(0, new BigDecimal("10").compareTo(batches.get(3).getTotalValueOfAllTransactions()));
        assertEquals(0, new BigDecimal("4").compareTo(batches.get(4).getTotalValueOfAllTransactions()));
    }
}