package com.volkanyungul.bank_account.integration;

import com.volkanyungul.bank_account.events.AuditReadyEvent;
import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import com.volkanyungul.bank_account.producer.scheduler.CreditTransactionScheduler;
import com.volkanyungul.bank_account.producer.scheduler.DebitTransactionScheduler;
import com.volkanyungul.bank_account.producer.scheduler.TransactionScheduler;
import lombok.SneakyThrows;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@TestConfiguration
public class EndToEndITConfig implements ApplicationListener<AuditReadyEvent> {

    private TransactionScheduler debitTransactionScheduler;

    private TransactionScheduler creditTransactionScheduler;

    private ScheduledExecutorService debitScheduledExecutorService;

    private ScheduledExecutorService creditScheduledExecutorService;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Bean
    @SneakyThrows
    public TransactionScheduler debitTransactionScheduler(TransactionGenerator transactionGenerator, ProducerProperties producerProperties, ApplicationEventPublisher applicationEventPublisher) {
        // Disabled TransactionSchedulerFactory in test profile, creating DEBIT scheduler bean manually to control (start/stop) the scheduled executor generating DEBIT transactions
        debitScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        debitTransactionScheduler = new DebitTransactionScheduler(transactionGenerator, producerProperties, applicationEventPublisher, debitScheduledExecutorService);
        return debitTransactionScheduler;
    }

    @Bean
    @SneakyThrows
    public TransactionScheduler creditTransactionScheduler(TransactionGenerator transactionGenerator, ProducerProperties producerProperties, ApplicationEventPublisher applicationEventPublisher) {
        // Disabled TransactionSchedulerFactory in test profile, creating CREDIT scheduler bean manually to control (start/stop) the scheduled executor generating CREDIT transactions
        creditScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        creditTransactionScheduler = new CreditTransactionScheduler(transactionGenerator, producerProperties, applicationEventPublisher, creditScheduledExecutorService);
        return creditTransactionScheduler;
    }

    @Bean
    @SneakyThrows
    public ApplicationListener<ContextRefreshedEvent> contextRefreshedListener() {
        return event -> {
            creditTransactionScheduler.schedule();
            debitTransactionScheduler.schedule();
            try {
                // Integration test thread will wait here till the transaction threshold(1000) is reached and audit is prepared
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    @Override
    public void onApplicationEvent(AuditReadyEvent event) {
        // Batch prepared, audit ready, shutdown transaction producing threads
        debitScheduledExecutorService.shutdownNow();
        creditScheduledExecutorService.shutdownNow();
        // Let the integration test continue to do the validations
        latch.countDown();
    }
}