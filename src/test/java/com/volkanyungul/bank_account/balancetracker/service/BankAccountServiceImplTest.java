package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.balancetracker.repository.BalanceRepository;
import com.volkanyungul.bank_account.events.TransactionProcessedEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static com.volkanyungul.bank_account.producer.dto.TransactionType.DEBIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Mock
    private BalanceRepository mockBalanceRepository;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    private Transaction transaction;

    private final AtomicInteger debitProcessCount = new AtomicInteger(0);

    private final AtomicInteger creditProcessCount = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        transaction = Transaction.builder().id("4f9f8356-e7d7-4245-b695-031a7daa493c").amount(new BigDecimal("397750.43")).transactionType(CREDIT).build();
    }

    @Test
    void shouldRunMultiThreadAndProcessTransactions() {
        // given
        // when
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        createThreadsAndTriggerProcessTransactionForTwoSeconds(executorService);
        // then
        int expectedExecutionCount = debitProcessCount.addAndGet(creditProcessCount.get());
        verify(mockBalanceRepository, times(expectedExecutionCount)).add(any());
        verify(mockApplicationEventPublisher, times(expectedExecutionCount)).publishEvent(any());
    }

    @Test
    void shouldSendBalanceToRepoAndTriggerPublisherWhenProcessingTransaction() {
        // given
        doNothing().when(mockBalanceRepository).add(any());
        // when
        bankAccountService.processTransaction(transaction);
        // then
        ArgumentCaptor<TransactionProcessedEvent> transactionProcessedEventArgumentCaptor = ArgumentCaptor.forClass(TransactionProcessedEvent.class);
        verify(mockApplicationEventPublisher, times(1)).publishEvent(transactionProcessedEventArgumentCaptor.capture());
        assertEquals(transaction, transactionProcessedEventArgumentCaptor.getValue().getTransaction());
        verify(mockBalanceRepository, times(1)).add(any());
    }

    @Test
    void shouldRetrieveBalanceFromRepository() {
        // given
        BigDecimal expectedBalance = new BigDecimal("397750.43");
        when(mockBalanceRepository.retrieve()).thenReturn(expectedBalance);
        // when
        double balance = bankAccountService.retrieveBalance();
        // then
        assertEquals(expectedBalance.doubleValue(), balance);
    }

    @SneakyThrows
    private void createThreadsAndTriggerProcessTransactionForTwoSeconds(ExecutorService executorService) {
        Runnable debitThread = () -> {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                bankAccountService.processTransaction(Transaction.builder().id("id1").transactionType(DEBIT).amount(new BigDecimal("-10.234")).build());
                debitProcessCount.incrementAndGet();
            }
        };
        Runnable creditThread = () -> {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                bankAccountService.processTransaction(Transaction.builder().id("id2").transactionType(CREDIT).amount(new BigDecimal("3432.35")).build());
                creditProcessCount.incrementAndGet();
            }
        };
        Future<?> debitFuture = executorService.submit(debitThread);
        Future<?> creditFuture = executorService.submit(creditThread);
        debitFuture.get();
        creditFuture.get();
    }
}