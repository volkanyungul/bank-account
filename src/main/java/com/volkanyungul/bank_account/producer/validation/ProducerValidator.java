package com.volkanyungul.bank_account.producer.validation;

public interface ProducerValidator<T> {

    void validate(T input);
}
