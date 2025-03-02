package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.config.ProducerTransactionConfig;
import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static com.volkanyungul.bank_account.producer.dto.TransactionType.DEBIT;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditTransactionSchedulerTest {

    private static final int AWAIT_DURATION = 4;

    @Mock
    private TransactionGenerator mockTransactionGenerator;

    @Mock
    private ProducerProperties mockProducerProperties;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Mock
    private ProducerTransactionConfig mockProducerTransactionConfig;

    @Mock
    private Transaction mockTransaction;

    @Test
    void shouldGenerateCreditTransactionAndPublish() {
        // given
        when(mockProducerProperties.getTransactionTypeConfigMap()).thenReturn(Map.of(CREDIT, mockProducerTransactionConfig, DEBIT, mockProducerTransactionConfig));
        when(mockProducerTransactionConfig.getTransactionCountPerSecond()).thenReturn(25L);
        when(mockProducerTransactionConfig.getTransactionAmountRange()).thenReturn(new Range(200L, 500000L));
        when(mockTransactionGenerator.generate(eq(CREDIT), any())).thenReturn(mockTransaction);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        CreditTransactionScheduler creditTransactionScheduler = new CreditTransactionScheduler(mockTransactionGenerator, mockProducerProperties, mockApplicationEventPublisher, scheduledExecutorService);
        // when
        creditTransactionScheduler.schedule();
        // then
        await().atMost(Duration.ofSeconds(AWAIT_DURATION)).untilAsserted(() -> {
            verify(mockTransactionGenerator, atLeastOnce()).generate(eq(CREDIT), any());

            ArgumentCaptor<TransactionCreatedEvent> transactionCreatedEventArgumentCaptor = ArgumentCaptor.forClass(TransactionCreatedEvent.class);
            verify(mockApplicationEventPublisher, atLeastOnce()).publishEvent(transactionCreatedEventArgumentCaptor.capture());
        });
    }
}

