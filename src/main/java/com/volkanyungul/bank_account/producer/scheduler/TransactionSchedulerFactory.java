package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.exception.BankAccountException;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import com.volkanyungul.bank_account.producer.validation.ProducerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(name="producer.scheduler.enabled", havingValue = "true", matchIfMissing = true)
@Profile("!test")
@Slf4j
@RequiredArgsConstructor
public class TransactionSchedulerFactory {

    private final ApplicationContext context;

    @Bean
    public TransactionScheduler debitTransactionScheduler(TransactionGenerator transactionGenerator, ProducerProperties producerProperties, ApplicationEventPublisher applicationEventPublisher, List<ProducerValidator<ProducerProperties>> producerValidators) {
        validate(producerValidators, producerProperties);

        var transactionScheduler = new DebitTransactionScheduler(transactionGenerator, producerProperties, applicationEventPublisher, Executors.newSingleThreadScheduledExecutor());
        transactionScheduler.schedule();
        return transactionScheduler;
    }

    @Bean
    public TransactionScheduler creditTransactionScheduler(TransactionGenerator transactionGenerator, ProducerProperties producerProperties, ApplicationEventPublisher applicationEventPublisher, List<ProducerValidator<ProducerProperties>> producerValidators) {
        validate(producerValidators, producerProperties);

        var transactionScheduler = new CreditTransactionScheduler(transactionGenerator, producerProperties, applicationEventPublisher, Executors.newSingleThreadScheduledExecutor());
        transactionScheduler.schedule();
        return transactionScheduler;
    }

    private void validate( List<ProducerValidator<ProducerProperties>> producerValidators, ProducerProperties producerProperties) {
        try {
            producerValidators.forEach(validator -> validator.validate(producerProperties));
        } catch (BankAccountException bankAccountException) {
            log.error("Error occurred on scheduling the Credit and Debit", bankAccountException);
            shutdownApplication();
        }
    }

    private void shutdownApplication() {
        int exitCode = SpringApplication.exit(context);
        System.exit(exitCode);
    }
}
