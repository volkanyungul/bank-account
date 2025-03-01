package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;

public interface TransactionGenerator {
    Transaction generate(TransactionType transactionType, Range range);
}
