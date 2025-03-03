package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static com.volkanyungul.bank_account.producer.dto.TransactionType.DEBIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomTransactionGeneratorTest {

    @InjectMocks
    private RandomTransactionGenerator randomTransactionGenerator;

    @Mock
    private Supplier<String> mockIdSupplier;

    @Mock
    private ThreadLocalRandom mockThreadLocalRandom;

    private MockedStatic<ThreadLocalRandom> threadLocalRandomMockedStatic;

    private final AtomicInteger debitTransactionCount = new AtomicInteger(0);

    private final AtomicInteger creditTransactionCount = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        when(mockIdSupplier.get()).thenReturn("transactionId");
        threadLocalRandomMockedStatic = Mockito.mockStatic(ThreadLocalRandom.class);
        threadLocalRandomMockedStatic.when(ThreadLocalRandom::current).thenReturn(mockThreadLocalRandom);
    }

    @AfterEach
    void close() {
        threadLocalRandomMockedStatic.close();
    }

    @Test
    void shouldBeAbleToGenerateCreditAndDebitTransactionsForTwoSecondsWithMultiThread() {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // when
        createThreadsAndRunForTwoSeconds(executorService);
        // then
        assertTrue(debitTransactionCount.get() > 0);
        assertTrue(creditTransactionCount.get() > 0);
    }

    @Test
    void shouldGenerateRandomIdAndAmountForCreditTransaction() {
        // given
        BigDecimal rangeMin = new BigDecimal("200");
        BigDecimal rangeMax = new BigDecimal("500000");
        when(mockThreadLocalRandom.nextLong(anyLong(), anyLong())).thenReturn(65348L);
        // when
        Transaction generatedTransaction = randomTransactionGenerator.generate(CREDIT, new Range(200L, 500000L));
        // then
        assertGeneratedTransactionAmount(generatedTransaction, rangeMin, rangeMax, CREDIT);
        assertEquals(new BigDecimal("653.48"), generatedTransaction.amount());
        assertEquals("transactionId", generatedTransaction.id());
    }

    @Test
    void shouldGenerateRandomIdAndAmountForDebitTransaction() {
        // given
        BigDecimal rangeMin = new BigDecimal("-500000");
        BigDecimal rangeMax = new BigDecimal("-200");
        when(mockThreadLocalRandom.nextLong(anyLong(), anyLong())).thenReturn(-245345L);
        // when
        Transaction generatedTransaction = randomTransactionGenerator.generate(DEBIT, new Range(-500000L, -200L));
        // then
        assertGeneratedTransactionAmount(generatedTransaction, rangeMin, rangeMax, DEBIT);
        assertEquals(new BigDecimal("-2453.45"), generatedTransaction.amount());
        assertEquals("transactionId", generatedTransaction.id());
    }

    @SneakyThrows
    private void createThreadsAndRunForTwoSeconds(ExecutorService executorService) {
        Runnable debitThread = () -> {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                randomTransactionGenerator.generate(DEBIT, new Range(-500000L, -200L));
                debitTransactionCount.incrementAndGet();
            }
        };
        Runnable creditThread = () -> {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                randomTransactionGenerator.generate(CREDIT, new Range(200L, 500000L));
                creditTransactionCount.incrementAndGet();
            }
        };
        Future<?> debitFuture = executorService.submit(debitThread);
        Future<?> creditFuture = executorService.submit(creditThread);
        debitFuture.get();
        creditFuture.get();
        executorService.shutdown();
    }

    private void assertGeneratedTransactionAmount(Transaction generatedTransaction, BigDecimal rangeMin, BigDecimal rangeMax, TransactionType expectedTransactionType) {
        BigDecimal generatedAmount = generatedTransaction.amount();
        assertTrue(generatedAmount.compareTo(rangeMin) >= 0);
        assertTrue(generatedAmount.compareTo(rangeMax) <= 0);
        assertEquals(expectedTransactionType, generatedTransaction.transactionType());
    }
}