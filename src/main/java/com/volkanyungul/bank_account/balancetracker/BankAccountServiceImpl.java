package com.volkanyungul.bank_account.balancetracker;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BankAccountServiceImpl implements BankAccountService, ApplicationListener<TransactionCreatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    @Override
    public void processTransaction(Transaction transaction) {
        logger.info("Transaction Received: {}" , transaction);
        // TODO: complete implementation
    }

    @Override
    public double retrieveBalance() {
        // TODO: complete implementation
        return 0;
    }

    @Override
    public void onApplicationEvent(TransactionCreatedEvent event) {
        this.processTransaction(event.getTransaction());
    }
}
