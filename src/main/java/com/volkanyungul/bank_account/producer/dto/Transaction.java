package com.volkanyungul.bank_account.producer.dto;

import java.math.BigDecimal;

public record Transaction(String id, BigDecimal amount, TransactionType transactionType) {
}
