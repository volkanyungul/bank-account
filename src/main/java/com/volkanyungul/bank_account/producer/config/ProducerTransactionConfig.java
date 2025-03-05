package com.volkanyungul.bank_account.producer.config;

import com.volkanyungul.bank_account.producer.dto.Range;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProducerTransactionConfig {
    private long transactionCountPerPeriod;
    private Range transactionAmountRange;
}
