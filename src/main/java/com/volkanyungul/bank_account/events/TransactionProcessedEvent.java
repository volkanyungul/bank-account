package com.volkanyungul.bank_account.events;

import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionProcessedEvent extends ApplicationEvent {

    private final transient Transaction transaction;

    public TransactionProcessedEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
