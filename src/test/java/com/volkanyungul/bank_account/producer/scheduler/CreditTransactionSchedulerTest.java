package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.config.ProducerSchedulerConfig;
import com.volkanyungul.bank_account.producer.config.ProducerTransactionConfig;
import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static com.volkanyungul.bank_account.producer.dto.TransactionType.DEBIT;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditTransactionSchedulerTest {

    @Mock
    private TransactionGenerator mockTransactionGenerator;

    @Mock
    private ProducerProperties mockProducerProperties;

    @Mock
    private ProducerSchedulerConfig mockProducerSchedulerConfig;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Mock
    private ProducerTransactionConfig mockProducerTransactionConfig;

    @Mock
    private Transaction mockTransaction;

    private CreditTransactionScheduler creditTransactionScheduler;

    @BeforeEach
    void setUp() {
        when(mockProducerProperties.getTransactionTypeConfigMap()).thenReturn(Map.of(CREDIT, mockProducerTransactionConfig, DEBIT, mockProducerTransactionConfig));
        when(mockProducerTransactionConfig.getTransactionAmountRange()).thenReturn(new Range(200L, 500000L));
        when(mockProducerProperties.getScheduler()).thenReturn(mockProducerSchedulerConfig);
        when(mockProducerSchedulerConfig.getInitialDelay()).thenReturn(0L);
        when(mockTransactionGenerator.generate(eq(CREDIT), any())).thenReturn(mockTransaction);
        creditTransactionScheduler = new CreditTransactionScheduler(mockTransactionGenerator, mockProducerProperties,
                mockApplicationEventPublisher, Executors.newSingleThreadScheduledExecutor());
    }

    @Test
    void shouldGenerateFiftyCreditTransactionsInTwoSecondsAndPublishAll() {
        // given
        long duration = 3L; // let the test run for 3 seconds, it will abort once verification is finished
        when(mockProducerSchedulerConfig.getPeriodInSeconds()).thenReturn(2L);
        when(mockProducerTransactionConfig.getTransactionCountPerPeriod()).thenReturn(25L);
        int expectedGeneratedTransactionCount = 50, expectedPublishedTransactionCount = 50;
        // when
        creditTransactionScheduler.schedule();
        // then
        await().atMost(Duration.ofSeconds(duration)).untilAsserted(() -> {
            verify(mockTransactionGenerator, times(expectedGeneratedTransactionCount)).generate(eq(CREDIT), any());

            ArgumentCaptor<TransactionCreatedEvent> transactionCreatedEventArgumentCaptor = ArgumentCaptor.forClass(TransactionCreatedEvent.class);
            verify(mockApplicationEventPublisher, times(expectedPublishedTransactionCount)).publishEvent(transactionCreatedEventArgumentCaptor.capture());
        });
    }
}

