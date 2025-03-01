package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;

import java.util.List;

public class RandomTransactionGenerator implements TransactionGenerator {

    private final TransactionType transactionType;
    private final Range range;

    public RandomTransactionGenerator(TransactionType transactionType, Range range) {
        this.transactionType = transactionType;
        this.range = range;
    }

    @Override
    public List<Transaction> generate(int numberOfTransactions) {
        System.out.println("transactionType: " + transactionType + ", range:" + range + ", numberOfTransactions: " + numberOfTransactions);
        return List.of();
    }
}
