package com.volkanyungul.bank_account.balancetracker.service;

import com.volkanyungul.bank_account.events.TransactionCreatedEvent;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionCreatedEventHandlerTest {

    @InjectMocks
    private TransactionCreatedEventHandler transactionCreatedEventHandler;

    @Mock
    private BankAccountService mockBankAccountService;

    @Mock
    private Transaction mockTransaction;

    @Test
    void shouldHandleOnApplicationEvent() {
        // given
        TransactionCreatedEvent transactionCreatedEvent = new TransactionCreatedEvent(this, mockTransaction);
        doNothing().when(mockBankAccountService).processTransaction(any(Transaction.class));
        // when
        transactionCreatedEventHandler.onApplicationEvent(transactionCreatedEvent);
        // then
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(mockBankAccountService, times(1)).processTransaction(transactionArgumentCaptor.capture());
    }
}