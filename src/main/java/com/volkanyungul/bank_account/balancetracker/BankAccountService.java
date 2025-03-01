package com.volkanyungul.bank_account.balancetracker;

import com.volkanyungul.bank_account.producer.dto.Transaction;

public interface BankAccountService {

    void processTransaction(Transaction transaction);

    double retrieveBalance();
}
