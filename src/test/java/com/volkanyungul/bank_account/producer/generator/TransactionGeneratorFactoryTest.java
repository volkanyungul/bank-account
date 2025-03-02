package com.volkanyungul.bank_account.producer.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TransactionGeneratorFactoryTest {

    private TransactionGeneratorFactory transactionGeneratorFactory;

    @BeforeEach
    void setUp() {
        transactionGeneratorFactory = new TransactionGeneratorFactory();
    }

    @Test
    void shouldCreateTransactionGenerator() {
        TransactionGenerator transactionGenerator = transactionGeneratorFactory.transactionGenerator();
        assertNotNull(transactionGenerator);
    }
}