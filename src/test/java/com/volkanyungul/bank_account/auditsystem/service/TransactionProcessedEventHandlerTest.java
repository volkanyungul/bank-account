package com.volkanyungul.bank_account.auditsystem.service;

import com.volkanyungul.bank_account.events.TransactionProcessedEvent;
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
class TransactionProcessedEventHandlerTest {

    @InjectMocks
    private TransactionProcessedEventHandler transactionProcessedEventHandler;

    @Mock
    private AuditManager mockAuditManager;

    @Mock
    private Transaction mockTransaction;

    @Test
    void shouldHandleOnApplicationEvent() {
        // given
        TransactionProcessedEvent transactionProcessedEvent = new TransactionProcessedEvent(this, mockTransaction);
        doNothing().when(mockAuditManager).receiveTransaction(any(Transaction.class));
        // when
        transactionProcessedEventHandler.onApplicationEvent(transactionProcessedEvent);
        // then
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(mockAuditManager, times(1)).receiveTransaction(transactionArgumentCaptor.capture());
    }
}