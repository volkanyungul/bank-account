package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.config.ProducerSchedulerConfig;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import com.volkanyungul.bank_account.producer.validation.ProducerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionSchedulerFactoryTest {

    @InjectMocks
    private TransactionSchedulerFactory transactionSchedulerFactory;

    private TransactionScheduler transactionScheduler;

    @Mock
    private TransactionGenerator mockTransactionGenerator;

    @Mock
    private ProducerProperties mockProducerProperties;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Mock
    private ProducerSchedulerConfig mockProducerSchedulerConfig;

    @Mock
    private List<ProducerValidator<ProducerProperties>> mockProducerValidators;

    @Mock
    private ApplicationContext mockApplicationContext;

    @BeforeEach
    void setUp() {
        transactionSchedulerFactory = new TransactionSchedulerFactory(mockApplicationContext);
        when(mockProducerProperties.getScheduler()).thenReturn(mockProducerSchedulerConfig);
        when(mockProducerSchedulerConfig.getInitialDelay()).thenReturn(0L);
        when(mockProducerSchedulerConfig.getPeriodInSeconds()).thenReturn(1L);
    }

    @Test
    void shouldCreateCreditTransactionScheduler() {
        transactionScheduler = transactionSchedulerFactory.creditTransactionScheduler(mockTransactionGenerator, mockProducerProperties, mockApplicationEventPublisher, mockProducerValidators);
        assertNotNull(transactionScheduler);
    }

    @Test
    void shouldCreateDebitTransactionScheduler() {
        transactionScheduler = transactionSchedulerFactory.debitTransactionScheduler(mockTransactionGenerator, mockProducerProperties, mockApplicationEventPublisher, mockProducerValidators);
        assertNotNull(transactionScheduler);
    }
}