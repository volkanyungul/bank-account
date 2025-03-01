package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebitSchedulerTest {

    @InjectMocks
    private DebitScheduler debitScheduler;

    @Mock
    private TransactionGenerator mockTransactionGenerator;

    @Mock
    private ProducerProperties mockProducerProperties;

    @Test
    void shouldTriggerTransactionGenerator() {
        // given
        when(mockProducerProperties.getTransactionCountPerSecond()).thenReturn(50);
        // when
        debitScheduler.schedule();
        // then
        verify(mockTransactionGenerator, times(1)).generate(25);
    }
}