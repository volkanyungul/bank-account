package com.volkanyungul.bank_account.producer.generator;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@AllArgsConstructor
public class RandomTransactionGenerator implements TransactionGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RandomTransactionGenerator.class);

    private final TransactionType transactionType;
    private final Range range;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void generate(int numberOfTransactions) {
        logger.info("transactionType: {}, range:{}, numberOfTransactions: {}", transactionType, range, numberOfTransactions);
        IntStream.range(0, numberOfTransactions)
                .forEach(i -> {
                    Transaction transaction = Transaction.builder().id(generateId()).amount(generateRandomAmount(range)).transactionType(transactionType).build();
                    applicationEventPublisher.publishEvent(new TransactionCreatedEvent(this, transaction));
                    logger.info("Transaction Published: {}" , transaction);
                });
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private BigDecimal generateRandomAmount(Range range) {
        long random;
        if(transactionType == TransactionType.CREDIT){
            random = new Random().nextLong(range.from() * 100, range.to() * 100);
        } else {
            random = new Random().nextLong(-range.to() * 100, -range.from() * 100);
        }
        return new BigDecimal(BigInteger.valueOf(random), 2);
    }
}
