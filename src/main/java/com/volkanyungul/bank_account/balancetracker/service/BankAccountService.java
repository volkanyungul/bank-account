package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.producer.dto.Transaction;

public interface BankAccountService {

    void processTransaction(Transaction transaction);

    double retrieveBalance();
}
