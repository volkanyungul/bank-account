package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TransactionSchedulerFactoryTest {

    private TransactionSchedulerFactory transactionSchedulerFactory;

    private TransactionScheduler transactionScheduler;

    @Mock
    private TransactionGenerator mockTransactionGenerator;

    @Mock
    private ProducerProperties mockProducerProperties;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;


    @BeforeEach
    void setUp() {
        transactionSchedulerFactory = new TransactionSchedulerFactory();
    }

    @Test
    void debitTransactionScheduler() {
        transactionScheduler = transactionSchedulerFactory.creditTransactionScheduler(mockTransactionGenerator, mockProducerProperties, mockApplicationEventPublisher);
        assertNotNull(transactionScheduler);
    }

    @Test
    void creditTransactionScheduler() {
        transactionScheduler = transactionSchedulerFactory.debitTransactionScheduler(mockTransactionGenerator, mockProducerProperties, mockApplicationEventPublisher);
        assertNotNull(transactionScheduler);
    }
}