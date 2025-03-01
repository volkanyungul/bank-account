package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.producer.dto.Transaction;

import java.util.List;

public interface TransactionGenerator {
    List<Transaction> generate(int numberOfTransactions);
}
