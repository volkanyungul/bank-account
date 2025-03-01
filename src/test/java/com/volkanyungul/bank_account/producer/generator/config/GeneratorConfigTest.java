package com.volkanyungul.bank_account.producer.generator.config;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.dto.Range;
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

    private ProducerProperties producerProperties;

    @BeforeEach
    void setUp() {
        producerProperties = new ProducerProperties();
        producerProperties.setTransactionAmountRange(new Range(200, 500000));
        producerProperties.setTransactionCountPerSecond(50);
        generatorConfig = new GeneratorConfig(mockApplicationEventPublisher, producerProperties);
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