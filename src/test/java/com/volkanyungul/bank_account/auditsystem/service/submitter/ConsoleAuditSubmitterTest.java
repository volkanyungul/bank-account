package com.volkanyungul.bank_account.auditsystem.service.submitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import com.volkanyungul.bank_account.auditsystem.dto.Batch;
import com.volkanyungul.bank_account.auditsystem.dto.Submission;
import com.volkanyungul.bank_account.producer.dto.Transaction;
import com.volkanyungul.bank_account.producer.dto.TransactionType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.volkanyungul.bank_account.producer.dto.TransactionType.CREDIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ConsoleAuditSubmitterTest {

    @InjectMocks
    private ConsoleAuditSubmitter consoleAuditSubmitter;

    @Mock
    private ObjectMapper mockObjectMapper;

    @Mock
    private ObjectWriter mockObjectWriter;

    private LogCaptor logCaptor;

    private Batch batch;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(ConsoleAuditSubmitter.class);
        consoleAuditSubmitter = new ConsoleAuditSubmitter(mockObjectMapper);
        batch = new Batch(new BigDecimal("12"));
        batch.addTransaction(Transaction.builder().id("123-abc").transactionType(CREDIT).amount(new BigDecimal("4546.34")).build());
        when(mockObjectMapper.writer()).thenReturn(mockObjectWriter);
        when(mockObjectWriter.withDefaultPrettyPrinter()).thenReturn(mockObjectWriter);
    }

    @Test
    @SneakyThrows
    void shouldPrintSingleBatch() {
        // given
        String expectedSingleBatchSubmission = """
            {
              "submission" : {
                "batches" : [ {
                  "totalValueOfAllTransactions" : 4546.34,
                  "countOfTransactions" : 1
                } ]
              }
            }""";
        when(mockObjectWriter.writeValueAsString(any())).thenReturn(expectedSingleBatchSubmission);
        // when
        consoleAuditSubmitter.submit(AuditSubmission.builder().submission(Submission.builder().batches(List.of(batch)).build()).build());
        // then
        assertEquals(expectedSingleBatchSubmission, logCaptor.getInfoLogs().getFirst());
    }

    @Test
    @SneakyThrows
    void shouldPrintSingleMultipleBatches() {
        // given
        String expectedMultiBatchSubmission = """
                {
                  "submission" : {
                    "batches" : [ {
                      "totalValueOfAllTransactions" : 4546.34,
                      "countOfTransactions" : 1
                    }, {
                      "totalValueOfAllTransactions" : 224.12,
                      "countOfTransactions" : 1
                    } ]
                  }
                }""";
        Batch batch2 = new Batch(new BigDecimal("15"));
        batch2.addTransaction(Transaction.builder().id("456-def").transactionType(TransactionType.DEBIT).amount(new BigDecimal("224.12")).build());
        when(mockObjectWriter.writeValueAsString(any())).thenReturn(expectedMultiBatchSubmission);
        // when
        consoleAuditSubmitter.submit(AuditSubmission.builder().submission(Submission.builder().batches(List.of(batch, batch2)).build()).build());
        // then
        assertEquals(expectedMultiBatchSubmission, logCaptor.getInfoLogs().getFirst());
    }

    @Test
    @SneakyThrows
    void shouldLogErrorWhenExceptionOccurredOnLogging() {
        // given
        when(mockObjectWriter.writeValueAsString(any())).thenThrow(new RuntimeException("Exception Occurred"));
        // when
        consoleAuditSubmitter.submit(new AuditSubmission(new Submission(List.of(batch))));
        // then
        assertEquals("Error happened while printing submission", logCaptor.getErrorLogs().getFirst());
    }
}