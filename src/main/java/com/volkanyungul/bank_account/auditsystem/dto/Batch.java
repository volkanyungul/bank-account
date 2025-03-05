package com.volkanyungul.bank_account.auditsystem.dto;

import com.volkanyungul.bank_account.producer.dto.Transaction;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Batch {
    private final AtomicReference<BigDecimal> totalValueOfAllTransactions = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicLong countOfTransactions = new AtomicLong(0L);
    private final BigDecimal valueOfTransactionLimit;

    public Batch(BigDecimal valueOfTransactionLimit) {
        this.valueOfTransactionLimit = valueOfTransactionLimit;
    }

    public void addTransaction(Transaction transaction) {
        totalValueOfAllTransactions.updateAndGet(current -> current.add(transaction.amount().abs()));
        countOfTransactions.incrementAndGet();
    }

    public BigDecimal getTotalValueOfAllTransactions() {
        return totalValueOfAllTransactions.get();
    }

    public Long getCountOfTransactions() {
        return countOfTransactions.get();
    }

    public boolean hasSpaceFor(Transaction transaction) {
        return getRemainingLimit().compareTo(transaction.amount().abs()) >= 0;
    }

    public BigDecimal getRemainingLimit() {
        return this.valueOfTransactionLimit.subtract(totalValueOfAllTransactions.get());
    }
}
