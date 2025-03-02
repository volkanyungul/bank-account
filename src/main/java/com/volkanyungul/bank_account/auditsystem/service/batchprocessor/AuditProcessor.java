package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.producer.dto.Transaction;

import java.util.PriorityQueue;

public interface AuditProcessor {

    void process(PriorityQueue<Transaction> auditTransactionsPriorityQueue);
}
