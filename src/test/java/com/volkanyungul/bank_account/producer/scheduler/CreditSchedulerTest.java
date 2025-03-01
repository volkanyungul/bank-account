package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreditSchedulerTest {

    @InjectMocks
    private CreditScheduler creditScheduler;

    @Mock
    private TransactionGenerator mockTransactionGenerator;

    @Test
    void shouldTriggerTransactionGenerator() {
        // given
        // when
        creditScheduler.schedule();
        // then
        verify(mockTransactionGenerator, times(1)).generate(25);
    }
}