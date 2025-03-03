package com.volkanyungul.bank_account.balancetracker.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@Slf4j
class InMemoryBalanceRepositoryTest {

    private InMemoryBalanceRepository inMemoryBalanceRepository;

    private final AtomicInteger debitOperationCount = new AtomicInteger(0);

    private final AtomicInteger creditOperationCount = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        inMemoryBalanceRepository = new InMemoryBalanceRepository();
    }

    @Test
    void shouldRunMultiThreadAndAddIntegerBalanceCorrectly() {
        // given
        BigDecimal debitAmount = new BigDecimal("-500");
        BigDecimal creditAmount =  new BigDecimal("1000");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // when
        createThreadsAndAddBalanceForTwoSeconds(executorService, debitAmount, creditAmount);
        // then
        verifyAmount(debitAmount, creditAmount);
    }

    @Test
    void shouldRunMultiThreadAndAddDecimalBalanceCorrectly() {
        // given
        BigDecimal debitAmount = new BigDecimal("-2345.32");
        BigDecimal creditAmount =  new BigDecimal("32235.64");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // when
        createThreadsAndAddBalanceForTwoSeconds(executorService, debitAmount, creditAmount);
        // then
        verifyAmount(debitAmount, creditAmount);
    }

    private void logTestResult(BigDecimal expectedDebitTotalAmount, BigDecimal expectedCreditTotalAmount) {
        log.info("""
                    DebitOperationCount: {}, CreditOperationCount: {}
                    ExpectedDebitTotalAmount: {}, ExpectedCreditTotalAmount: {}
                    Expected Result (credit amount - debit amount): {}, Actual Result(DB Amount): {}
                 """, debitOperationCount.get(), creditOperationCount.get(),
                expectedDebitTotalAmount, expectedCreditTotalAmount, expectedCreditTotalAmount.add(expectedDebitTotalAmount), inMemoryBalanceRepository.retrieve());
    }

    @Test
    void shouldAddGivenBigDecimalIntoInMemoryBalance() {
        // when
        inMemoryBalanceRepository.add(new BigDecimal("1234.56"));
        inMemoryBalanceRepository.add(new BigDecimal("1234.56"));
        // then
        assertEquals(new BigDecimal("2469.12").doubleValue(), inMemoryBalanceRepository.getBalance().get().doubleValue());
    }

    @Test
    void shouldRetrieveTheCurrentBalance() {
        // when
        inMemoryBalanceRepository.add(new BigDecimal("1234.56"));
        inMemoryBalanceRepository.add(new BigDecimal("1234.56"));
        // then
        assertEquals(new BigDecimal("2469.12").doubleValue(), inMemoryBalanceRepository.retrieve().doubleValue());
    }

    @SneakyThrows
    private void createThreadsAndAddBalanceForTwoSeconds(ExecutorService executorService, BigDecimal debitAmount, BigDecimal creditAmount) {
        Runnable debitThread = () -> {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                inMemoryBalanceRepository.add(debitAmount);
                debitOperationCount.incrementAndGet();
            }
        };
        Runnable creditThread = () -> {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                inMemoryBalanceRepository.add(creditAmount);
                creditOperationCount.incrementAndGet();
            }
        };
        Future<?> debitFuture = executorService.submit(debitThread);
        Future<?> creditFuture = executorService.submit(creditThread);
        debitFuture.get();
        creditFuture.get();
    }

    private void verifyAmount(BigDecimal debitAmount, BigDecimal creditAmount) {
        BigDecimal expectedDebitTotalAmount = debitAmount.multiply(BigDecimal.valueOf(debitOperationCount.get()));
        BigDecimal expectedCreditTotalAmount = creditAmount.multiply(BigDecimal.valueOf(creditOperationCount.get()));
        logTestResult(expectedDebitTotalAmount, expectedCreditTotalAmount);
        assertEquals(expectedCreditTotalAmount.add(expectedDebitTotalAmount), inMemoryBalanceRepository.retrieve());
    }
}
