package com.volkanyungul.bank_account.auditsystem.service;

import com.volkanyungul.bank_account.auditsystem.config.AuditSystemProperties;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.service.batchprocessor.BatchProcessor;
import com.volkanyungul.bank_account.auditsystem.service.submitter.ConsoleLoggingAuditSubmitter;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditManagerTest {

    @InjectMocks
    private AuditManager auditManager;

    @Mock
    private BatchProcessor mockBatchProcessor;

    @Mock
    private AuditSystemProperties mockAuditSystemProperties;

    @Mock
    private ConsoleLoggingAuditSubmitter mockConsoleLoggingAuditSubmitter;

    private Field transactionPriorityQueueField;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        transaction1 = Transaction.builder().id("id1").transactionType(CREDIT).amount(new BigDecimal("11")).build();
        transaction2 = Transaction.builder().id("id2").transactionType(CREDIT).amount(new BigDecimal("10")).build();
        transaction3 = Transaction.builder().id("id3").transactionType(CREDIT).amount(new BigDecimal("9")).build();
        transactionPriorityQueueField = AuditManager.class.getDeclaredField("transactionPriorityQueue");
        transactionPriorityQueueField.setAccessible(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSortTheAddedTransactionsAndSendToAuditProcessorInCorrectOrder() {
        // given
        when(mockAuditSystemProperties.getTransactionCountThreshold()).thenReturn(2L);
        String smallAmountTransactionId = "id1";
        String bigAmountTransactionId = "id2";
        Transaction smallAmountTransaction = Transaction.builder().id(smallAmountTransactionId).transactionType(CREDIT).amount(new BigDecimal("100")).build();
        Transaction highAmountTransaction = Transaction.builder().id(bigAmountTransactionId).transactionType(CREDIT).amount(new BigDecimal("200")).build();
        when(mockBatchProcessor.process(any())).thenReturn(CompletableFuture.completedFuture(List.of(new Batch(new BigDecimal("10")), new Batch(new BigDecimal("20")))));
        // when
        auditManager.receiveTransaction(smallAmountTransaction);
        auditManager.receiveTransaction(highAmountTransaction);
        // then
        ArgumentCaptor<PriorityQueue<Transaction>> priorityQueueArgumentCaptor = ArgumentCaptor.forClass(PriorityQueue.class);
        verify(mockBatchProcessor, times(1)).process(priorityQueueArgumentCaptor.capture());
        PriorityQueue<Transaction> auditProcessorSubmittedTransactions = priorityQueueArgumentCaptor.getValue();
        assertFalse(auditProcessorSubmittedTransactions.isEmpty());

        assertEquals(2, auditProcessorSubmittedTransactions.size(), "Both transactions are sent to be process since the threshold is 2");

        assert auditProcessorSubmittedTransactions.peek() != null;
        assertEquals(bigAmountTransactionId, auditProcessorSubmittedTransactions.poll().id(), "Big Amount Transaction should come first");
        assert auditProcessorSubmittedTransactions.peek() != null;
        assertEquals(smallAmountTransactionId, auditProcessorSubmittedTransactions.poll().id(), "Small Amount Transaction should come next");
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldReceiveTransactionWhenTransactionCountNotReachedTheThreshold() {
        // given
        // when
        when(mockAuditSystemProperties.getTransactionCountThreshold()).thenReturn(3L);
        auditManager.receiveTransaction(transaction1);
        auditManager.receiveTransaction(transaction2);
        // then
        PriorityQueue<Transaction> transactionPriorityQueue = (PriorityQueue<Transaction>) transactionPriorityQueueField.get(auditManager);
        // Both transactions will remain in the queue since not reached the threshold
        assertTrue(transactionPriorityQueue.contains(transaction1));
        assertTrue(transactionPriorityQueue.contains(transaction2));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldReceiveTransactionWhenTransactionCountReachedTheThreshold() {
        // given
        when(mockAuditSystemProperties.getTransactionCountThreshold()).thenReturn(2L);
        when(mockBatchProcessor.process(any())).thenReturn(CompletableFuture.completedFuture(List.of(new Batch(new BigDecimal("10")), new Batch(new BigDecimal("20")))));
        // when
        auditManager.receiveTransaction(transaction1);
        auditManager.receiveTransaction(transaction2);
        auditManager.receiveTransaction(transaction3);
        // then
        PriorityQueue<Transaction> transactionPriorityQueue = (PriorityQueue<Transaction>) transactionPriorityQueueField.get(auditManager);
        // The queue will be reset after the first two transactions, only third transaction will left in the queue
        assertFalse(transactionPriorityQueue.contains(transaction1));
        assertFalse(transactionPriorityQueue.contains(transaction2));
        assertTrue(transactionPriorityQueue.contains(transaction3));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldTestQueueEmptyWhenAllTheTransactionsAreSubmittedToTheAuditProcessor() {
        // given
        when(mockAuditSystemProperties.getTransactionCountThreshold()).thenReturn(2L);
        when(mockBatchProcessor.process(any())).thenReturn(CompletableFuture.completedFuture(List.of(new Batch(new BigDecimal("10")), new Batch(new BigDecimal("20")))));
        // when
        auditManager.receiveTransaction(transaction1);
        auditManager.receiveTransaction(transaction2);
        // then
        PriorityQueue<Transaction> transactionPriorityQueue = (PriorityQueue<Transaction>) transactionPriorityQueueField.get(auditManager);
        assertTrue(transactionPriorityQueue.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void prepareBatchAndResetQueue() {
        // given
        when(mockAuditSystemProperties.getTransactionCountThreshold()).thenReturn(2L);
        when(mockBatchProcessor.process(any())).thenReturn(CompletableFuture.completedFuture(List.of(new Batch(new BigDecimal("10")), new Batch(new BigDecimal("20")))));
        // when
        auditManager.receiveTransaction(transaction1);
        auditManager.receiveTransaction(transaction2);
        // then
        ArgumentCaptor<PriorityQueue<Transaction>> priorityQueueArgumentCaptor = ArgumentCaptor.forClass(PriorityQueue.class);
        verify(mockBatchProcessor, times(1)).process(priorityQueueArgumentCaptor.capture());
        PriorityQueue<Transaction> auditProcessorSubmittedTransactions = priorityQueueArgumentCaptor.getValue();
        assertFalse(auditProcessorSubmittedTransactions.isEmpty());

        assertEquals(2, auditProcessorSubmittedTransactions.size());

        assert auditProcessorSubmittedTransactions.peek() != null;
        assertEquals("id1", auditProcessorSubmittedTransactions.poll().id());
        assert auditProcessorSubmittedTransactions.peek() != null;
        assertEquals("id2", auditProcessorSubmittedTransactions.poll().id());
    }
}