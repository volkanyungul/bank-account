package com.volkanyungul.bank_account.producer.validation;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.dto.Range;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import com.volkanyungul.bank_account.producer.exception.TransactionAmountNotValidException;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeMapValidator implements ProducerValidator<ProducerProperties> {

    private static final String ERR_MSG_AMOUNT_FROM_BIGGER_THAN_TO_IN_RANGE = "-> Transaction amount range is not valid. Range 'from' should be smaller than 'to'";
    private static final String ERR_MSG_AMOUNT_NOT_DEBIT_OR_CREDIT_RANGE = "-> Transaction amount range is not valid. Both 'from' and 'to' should be positive for Credit, negative for Debit";

    @Override
    public void validate(ProducerProperties producerProperties) {
        producerProperties.getTransactionTypeConfigMap().forEach((transactionType, producerTransactionConfig) ->
                validateRange(transactionType, producerTransactionConfig.getTransactionAmountRange()));
    }

    private void validateRange(TransactionType transactionType, Range range) {
        if(range.from() > range.to()) {
            throw new TransactionAmountNotValidException(transactionType + ERR_MSG_AMOUNT_FROM_BIGGER_THAN_TO_IN_RANGE);
        }
        if(range.from() < 0 && range.to() > 0) {
            throw new TransactionAmountNotValidException(transactionType + ERR_MSG_AMOUNT_NOT_DEBIT_OR_CREDIT_RANGE);
        }
    }
}
