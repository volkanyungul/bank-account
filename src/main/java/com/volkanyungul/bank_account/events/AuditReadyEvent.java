package com.volkanyungul.bank_account.events;

import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.PriorityQueue;

@Getter
public class AuditReadyEvent extends ApplicationEvent {

    private final transient List<Batch> batches;

    private final transient PriorityQueue<Transaction> transactions;

    public AuditReadyEvent(Object source, List<Batch> batches, PriorityQueue<Transaction> transactions) {
        super(source);
        this.batches = batches;
        this.transactions = transactions;
    }
}
