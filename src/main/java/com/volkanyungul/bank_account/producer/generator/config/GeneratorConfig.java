package com.volkanyungul.bank_account.producer.generator.config;

import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import com.volkanyungul.bank_account.producer.generator.RandomTransactionGenerator;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneratorConfig {

    @Bean
    public TransactionGenerator debitTransactionGenerator() {
        return new RandomTransactionGenerator(TransactionType.DEBIT, new Range(200, 500000));
    }

    @Bean
    public TransactionGenerator creditTransactionGenerator() {
        return new RandomTransactionGenerator(TransactionType.CREDIT, new Range(200, 500000));
    }
}
