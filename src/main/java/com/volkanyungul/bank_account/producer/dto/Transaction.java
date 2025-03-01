package com.volkanyungul.bank_account.producer.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Transaction(String id, BigDecimal amount, TransactionType transactionType) {
}
