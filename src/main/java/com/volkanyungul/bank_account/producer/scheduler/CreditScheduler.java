package com.volkanyungul.bank_account.producer.scheduler;

import com.volkanyungul.bank_account.producer.generator.TransactionGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CreditScheduler implements Scheduler {

    private final TransactionGenerator transactionGenerator;

    public CreditScheduler(@Qualifier("creditTransactionGenerator") TransactionGenerator transactionGenerator) {
        this.transactionGenerator = transactionGenerator;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void schedule() {
        transactionGenerator.generate(25);
    }
}
