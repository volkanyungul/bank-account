package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class RandomTransactionGenerator implements TransactionGenerator {

    private final Supplier<String> idSupplier;

    @Override
    public Transaction generate(TransactionType transactionType, Range range) {
        return Transaction.builder().id(idSupplier.get()).amount(generateRandomAmount(range)).transactionType(transactionType).build();
    }

    private BigDecimal generateRandomAmount(Range range) {
        long randomAmount = ThreadLocalRandom.current().nextLong(range.from() * 100, range.to() * 100);
        return new BigDecimal(BigInteger.valueOf(randomAmount), 2);
    }
}
