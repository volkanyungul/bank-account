package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.config.ProducerSchedulerConfig;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;

@RequiredArgsConstructor
public class CreditTransactionScheduler implements TransactionScheduler {

    private final TransactionGenerator transactionGenerator;

    private final ProducerProperties producerProperties;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ScheduledExecutorService scheduledExecutorService;

    @Override
    public void schedule() {
        ProducerSchedulerConfig scheduler = producerProperties.getScheduler();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            var transactionConfig = producerProperties.getTransactionTypeConfigMap().get(CREDIT);
            LongStream.range(0, transactionConfig.getTransactionCountPerPeriod())
                    .mapToObj(i -> transactionGenerator.generate(CREDIT, transactionConfig.getTransactionAmountRange()))
                    .forEach(transaction -> applicationEventPublisher.publishEvent(new TransactionCreatedEvent(this, transaction)));
        }, scheduler.getInitialDelay(), scheduler.getPeriodInSeconds(), TimeUnit.SECONDS);
    }
}
