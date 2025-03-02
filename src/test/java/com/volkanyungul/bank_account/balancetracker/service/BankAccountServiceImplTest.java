package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.balancetracker.repository.BalanceRepository;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Mock
    private BalanceRepository balanceRepository;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = Transaction.builder().id("4f9f8356-e7d7-4245-b695-031a7daa493c").amount(new BigDecimal("397750.43")).transactionType(CREDIT).build();
    }

    @Test
    void processTransaction() {
        doNothing().when(balanceRepository).add(any());
        bankAccountService.processTransaction(transaction);
    }

    @Test
    void retrieveBalance() {
        BigDecimal expectedBalance = new BigDecimal("397750.43");
        when(balanceRepository.retrieve()).thenReturn(expectedBalance);
        double balance = bankAccountService.retrieveBalance();
        assertEquals(expectedBalance.doubleValue(), balance);
    }
}