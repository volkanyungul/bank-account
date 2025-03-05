package com.volkanyungul.bank_account.producer.exception;

public class TransactionAmountNotValidException extends BankAccountException {

    public TransactionAmountNotValidException(String message) {
        super(message);
    }
}
