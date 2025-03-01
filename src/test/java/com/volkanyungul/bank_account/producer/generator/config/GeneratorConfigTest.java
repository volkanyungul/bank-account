package com.volkanyungul.bank_account.producer.generator.config;

import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GeneratorConfigTest {

    private GeneratorConfig generatorConfig;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @BeforeEach
    void setUp() {
        generatorConfig = new GeneratorConfig(mockApplicationEventPublisher);
    }

    @Test
    void shouldCreateDebitTransactionGenerator() {
        TransactionGenerator debitTransactionGenerator = generatorConfig.debitTransactionGenerator();
        assertNotNull(debitTransactionGenerator);
    }

    @Test
    void creditTransactionGenerator() {
        TransactionGenerator debitTransactionGenerator = generatorConfig.creditTransactionGenerator();
        assertNotNull(debitTransactionGenerator);
    }
}