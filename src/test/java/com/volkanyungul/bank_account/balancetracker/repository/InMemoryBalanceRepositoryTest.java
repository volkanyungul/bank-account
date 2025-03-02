package com.volkanyungul.bank_account.balancetracker.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryBalanceRepositoryTest {

    private InMemoryBalanceRepository inMemoryBalanceRepository;

    @BeforeEach
    void setUp() {
        inMemoryBalanceRepository = new InMemoryBalanceRepository();
        inMemoryBalanceRepository.add(new BigDecimal("1234.56"));
        inMemoryBalanceRepository.add(new BigDecimal("1234.56"));
    }

    @Test
    void shouldAddGivenBigDecimalIntoInMemoryBalance() {
        assertEquals(new BigDecimal("2469.12").doubleValue(), inMemoryBalanceRepository.getBalance().get().doubleValue());
    }

    @Test
    void shouldRetrieveTheCurrentBalance() {
        assertEquals(new BigDecimal("2469.12").doubleValue(), inMemoryBalanceRepository.retrieve().doubleValue());
    }
}