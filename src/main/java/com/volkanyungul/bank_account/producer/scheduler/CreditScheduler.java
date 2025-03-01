package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CreditScheduler implements Scheduler {

    private final TransactionGenerator transactionGenerator;

    private final ProducerProperties producerProperties;

    public CreditScheduler(@Qualifier("creditTransactionGenerator") TransactionGenerator transactionGenerator, ProducerProperties producerProperties) {
        this.transactionGenerator = transactionGenerator;
        this.producerProperties = producerProperties;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void schedule() {
        long numberOfTransactions = producerProperties.getTransactionCountPerSecond() / TransactionType.values().length;
        transactionGenerator.generate(numberOfTransactions);
    }
}
