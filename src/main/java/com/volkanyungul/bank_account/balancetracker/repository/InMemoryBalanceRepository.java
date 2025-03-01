package com.volkanyungul.bank_account.balancetracker.repository;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class InMemoryBalanceRepository implements BalanceRepository {

    private AtomicReference<BigDecimal> balance = new AtomicReference<>(BigDecimal.ZERO);

    @Override
    public void add(BigDecimal amount) {
        balance.updateAndGet(current -> current.add(amount));
    }

    @Override
    public BigDecimal retrieve() {
        return balance.get();
    }
}
