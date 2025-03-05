package com.volkanyungul.bank_account.auditsystem.service.batchprocessor;

import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.producer.dto.Transaction;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;

public interface BatchProcessor {

    CompletableFuture<List<Batch>> process(PriorityQueue<Transaction> auditTransactionsPriorityQueue);
}
