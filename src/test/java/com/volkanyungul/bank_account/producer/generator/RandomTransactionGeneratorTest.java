package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

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

    MockedStatic<ThreadLocalRandom> threadLocalRandomMockedStatic;

    @BeforeEach
    void setUp() {
        when(mockIdSupplier.get()).thenReturn("debit-123-abc");
        threadLocalRandomMockedStatic = Mockito.mockStatic(ThreadLocalRandom.class);
        threadLocalRandomMockedStatic.when(ThreadLocalRandom::current).thenReturn(mockThreadLocalRandom);
    }

    @AfterEach
    void close() {
        threadLocalRandomMockedStatic.close();
    }

    @Test
    void shouldGenerateCreditTransaction() {
        // given
        BigDecimal minThreshold = new BigDecimal("200");
        BigDecimal maxThreshold = new BigDecimal("500000");
        when(mockThreadLocalRandom.nextLong(anyLong(), anyLong())).thenReturn(65348L);
        // when
        Transaction generatedTransaction = randomTransactionGenerator.generate(TransactionType.CREDIT, new Range(200L, 500000L));
        // then
        assertGeneratedTransaction(generatedTransaction, minThreshold, maxThreshold, TransactionType.CREDIT);
        assertEquals(new BigDecimal("653.48"), generatedTransaction.amount());
    }

    @Test
    void shouldGenerateDebitTransaction() {
        // given
        BigDecimal minThreshold = new BigDecimal("-500000");
        BigDecimal maxThreshold = new BigDecimal("-200");
        when(mockThreadLocalRandom.nextLong(anyLong(), anyLong())).thenReturn(-245345L);
        // when
        Transaction generatedTransaction = randomTransactionGenerator.generate(TransactionType.DEBIT, new Range(-500000L, -200L));
        // then
        assertGeneratedTransaction(generatedTransaction, minThreshold, maxThreshold, TransactionType.DEBIT);
        assertEquals(new BigDecimal("-2453.45"), generatedTransaction.amount());
    }

    private void assertGeneratedTransaction(Transaction generatedTransaction, BigDecimal minThreshold, BigDecimal maxThreshold, TransactionType expectedTransactionType) {
        BigDecimal generatedAmount = generatedTransaction.amount();
        assertTrue(generatedAmount.compareTo(minThreshold) >= 0);
        assertTrue(generatedAmount.compareTo(maxThreshold) <= 0);
        assertEquals(expectedTransactionType, generatedTransaction.transactionType());
    }
}