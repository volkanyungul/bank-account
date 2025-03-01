package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class TransactionSchedulerFactory {

    @Bean
    public TransactionScheduler debitTransactionScheduler(TransactionGenerator transactionGenerator, ProducerProperties producerProperties, ApplicationEventPublisher applicationEventPublisher) {
        var transactionScheduler = new DebitTransactionScheduler(transactionGenerator, producerProperties, applicationEventPublisher, Executors.newSingleThreadScheduledExecutor());
        transactionScheduler.schedule();
        return transactionScheduler;
    }

    @Bean
    public TransactionScheduler creditTransactionScheduler(TransactionGenerator transactionGenerator, ProducerProperties producerProperties, ApplicationEventPublisher applicationEventPublisher) {
        var transactionScheduler = new CreditTransactionScheduler(transactionGenerator, producerProperties, applicationEventPublisher, Executors.newSingleThreadScheduledExecutor());
        transactionScheduler.schedule();
        return transactionScheduler;
    }
}
