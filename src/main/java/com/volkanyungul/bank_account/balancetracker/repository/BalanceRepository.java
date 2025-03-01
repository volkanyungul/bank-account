package com.volkanyungul.bank_account.balancetracker.repository;

import java.math.BigDecimal;

public interface BalanceRepository {

    void add(BigDecimal amount);

    BigDecimal retrieve();
}
