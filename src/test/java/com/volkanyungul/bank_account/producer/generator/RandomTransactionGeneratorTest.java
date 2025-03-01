package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RandomTransactionGeneratorTest {

    private RandomTransactionGenerator creditRandomTransactionGenerator;
    private RandomTransactionGenerator debitRandomTransactionGenerator;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setUp() {
        creditRandomTransactionGenerator = new RandomTransactionGenerator(TransactionType.CREDIT, new Range(200, 500000), applicationEventPublisher);
        debitRandomTransactionGenerator = new RandomTransactionGenerator(TransactionType.DEBIT, new Range(200, 500000), applicationEventPublisher);
    }

    @Test
    void shouldGenerateCreditTransactionsAndPublish() {
        // given
        // when
        creditRandomTransactionGenerator.generate(25);
        // then
        ArgumentCaptor<TransactionCreatedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(TransactionCreatedEvent.class);
        verify(applicationEventPublisher, times(25)).publishEvent(eventArgumentCaptor.capture());

        for(TransactionCreatedEvent transactionCreatedEvent : eventArgumentCaptor.getAllValues()) {
            assertTrue(transactionCreatedEvent.getTransaction().amount().compareTo(new BigDecimal("200")) >= 0);
            assertTrue(transactionCreatedEvent.getTransaction().amount().compareTo(new BigDecimal("500000")) <= 0);
            assertEquals(TransactionType.CREDIT, transactionCreatedEvent.getTransaction().transactionType());
        }
    }

    @Test
    void shouldGenerateDebitTransactionAndPublish() {
        // given
        // when
        debitRandomTransactionGenerator.generate(25);
        // then
        ArgumentCaptor<TransactionCreatedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(TransactionCreatedEvent.class);
        verify(applicationEventPublisher, times(25)).publishEvent(eventArgumentCaptor.capture());

        for(TransactionCreatedEvent transactionCreatedEvent : eventArgumentCaptor.getAllValues()) {
            assertTrue(transactionCreatedEvent.getTransaction().amount().compareTo(new BigDecimal("-500000")) >= 0);
            assertTrue(transactionCreatedEvent.getTransaction().amount().compareTo(new BigDecimal("-200")) <= 0);
            assertEquals(TransactionType.DEBIT, transactionCreatedEvent.getTransaction().transactionType());
        }
    }

    @Test
    void shouldGenerateIdAndAmountRandomlyForDebitTransaction() {
        // given
        // when
        debitRandomTransactionGenerator.generate(1);
        // then
        ArgumentCaptor<TransactionCreatedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(TransactionCreatedEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventArgumentCaptor.capture());

        for(TransactionCreatedEvent transactionCreatedEvent : eventArgumentCaptor.getAllValues()) {
            assertNotNull(transactionCreatedEvent.getTransaction().id());
            assertTrue(transactionCreatedEvent.getTransaction().amount().compareTo(BigDecimal.ZERO) < 0);
        }
    }

    @Test
    void shouldGenerateIdAndAmountRandomlyForCreditTransaction() {
        // given
        // when
        creditRandomTransactionGenerator.generate(1);
        // then
        ArgumentCaptor<TransactionCreatedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(TransactionCreatedEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventArgumentCaptor.capture());

        for(TransactionCreatedEvent transactionCreatedEvent : eventArgumentCaptor.getAllValues()) {
            assertNotNull(transactionCreatedEvent.getTransaction().id());
            assertTrue(transactionCreatedEvent.getTransaction().amount().compareTo(BigDecimal.ZERO) > 0);
        }
    }
}