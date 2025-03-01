package com.volkanyungul.bank_account.producer.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class TransactionGeneratorFactory {

    @Bean
    public TransactionGenerator transactionGenerator() {
        return new RandomTransactionGenerator(() -> UUID.randomUUID().toString());
    }
}
