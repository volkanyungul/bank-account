package com.volkanyungul.bank_account.integration;

import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.events.AuditReadyEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.PriorityQueue;

@Component
@Getter
public class AuditSubmissionITEventListener implements ApplicationListener<AuditReadyEvent> {

    private List<Batch> batches;

    private PriorityQueue<Transaction> transactions;

    @Override
    public void onApplicationEvent(AuditReadyEvent event) {
        this.batches = event.getBatches();
        this.transactions = event.getTransactions();
    }
}