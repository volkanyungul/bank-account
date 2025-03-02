package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.service.submission.ConsoleAuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchAuditProcessorTest {

    @Mock
    private AuditSystemProperties mockAuditSystemProperties;

    @Mock
    private ConsoleAuditSubmitter mockConsoleAuditSubmissionService;

    @Test
    void process() {
        PriorityQueue<Transaction> auditTransactionsPriorityQueue =
                new PriorityQueue<>(Comparator.comparing(transaction -> transaction.amount().abs(), Comparator.reverseOrder()));

        // Add transactions to the queue, amounts [9 , 8 , 8 , 5, 3, 2, 2, 1, 1]
        auditTransactionsPriorityQueue.add(Transaction.builder().id("123").transactionType(TransactionType.CREDIT).amount(new BigDecimal("9")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("456").transactionType(TransactionType.DEBIT).amount(new BigDecimal("-8")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("789").transactionType(TransactionType.CREDIT).amount(new BigDecimal("8")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("135").transactionType(TransactionType.DEBIT).amount(new BigDecimal("-5")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("135").transactionType(TransactionType.CREDIT).amount(new BigDecimal("3")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("135").transactionType(TransactionType.DEBIT).amount(new BigDecimal("-2")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("135").transactionType(TransactionType.CREDIT).amount(new BigDecimal("2")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("135").transactionType(TransactionType.DEBIT).amount(new BigDecimal("-1")).build());
        auditTransactionsPriorityQueue.add(Transaction.builder().id("135").transactionType(TransactionType.CREDIT).amount(new BigDecimal("1")).build());

        BatchAuditProcessor batchAuditProcessor = new BatchAuditProcessor(mockAuditSystemProperties, mockConsoleAuditSubmissionService);
        when(mockAuditSystemProperties.getTotalValueOfAllTransactionsThreshold()).thenReturn(new BigDecimal("10"));

        // when
        batchAuditProcessor.process(auditTransactionsPriorityQueue);

        // then
        ArgumentCaptor<AuditSubmission> auditSubmissionArgumentCaptor = ArgumentCaptor.forClass(AuditSubmission.class);
        verify(mockConsoleAuditSubmissionService, times(1)).submit(auditSubmissionArgumentCaptor.capture());
        AuditSubmission auditSubmission = auditSubmissionArgumentCaptor.getValue();
        // Amounts in the transactions are like [9 , 8 , 8 , 5, 3, 2, 2, 1, 1], and totalValueOfAllTransactionsThreshold is 10.
        // Created batches includes below amounts
        // Batch 1 -> [9,1]
        // Batch 2 -> [8,2]
        // Batch 3 -> [8,2]
        // Batch 4 -> [5,3,1]
        List<Batch> batches = auditSubmission.batches();
        assertEquals(4, batches.size());
        assertEquals(2, batches.get(0).getCountOfTransactions().intValue());
        assertEquals(2, batches.get(1).getCountOfTransactions().intValue());
        assertEquals(2, batches.get(2).getCountOfTransactions().intValue());
        assertEquals(3, batches.get(3).getCountOfTransactions().intValue());
        assertEquals(10, batches.get(0).getTotalValueOfAllTransactions().intValue());
        assertEquals(10, batches.get(1).getTotalValueOfAllTransactions().intValue());
        assertEquals(10, batches.get(2).getTotalValueOfAllTransactions().intValue());
        assertEquals(9, batches.get(3).getTotalValueOfAllTransactions().intValue());
    }
}