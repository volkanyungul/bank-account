package com.volkanyungul.bank_account.producer.generator.config;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.generator.RandomTransactionGenerator;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static com.volkanyungul.bank_account.producer.dto.TransactionType.DEBIT;

@Configuration
public class GeneratorConfig {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ProducerProperties producerProperties;

    private Range range;

    public GeneratorConfig(ApplicationEventPublisher applicationEventPublisher, ProducerProperties producerProperties) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.producerProperties = producerProperties;
    }

    @PostConstruct
    public void postConstruct() {
        Range txnAmountRange = this.producerProperties.getTransactionAmountRange();
        range = new Range(txnAmountRange.from(), txnAmountRange.to());
    }

    @Bean
    public TransactionGenerator debitTransactionGenerator() {
        return new RandomTransactionGenerator(DEBIT, range, applicationEventPublisher);
    }

    @Bean
    public TransactionGenerator creditTransactionGenerator() {
        return new RandomTransactionGenerator(CREDIT, range, applicationEventPublisher);
    }
}
