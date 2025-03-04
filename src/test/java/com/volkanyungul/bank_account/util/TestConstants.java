package com.volkanyungul.bank_account.util;

public class TestConstants {

    private TestConstants() {}

    public static final String PERFORMANCE_CONSOLE_LOG_FORMAT = """
                --------------------------------------------------------------
                --------------------------------------------------------------
                Config
                --------------------------------------------------------------
                Thread Count: {}
                Thread Running Period: {} (second)
                Credit Transaction Count: {} (per second)
                Debit Transaction Count: {} (per second)
                Transaction Count Threshold: {}
                Transaction Total Value Of All Transactions Threshold: {}
                --------------------------------------------------------------
                TRANSACTION OVERVIEW
                --------------------------------------------------------------
                Processed Transaction Count: {}
                Transaction Total Amount: {}
                --------------------------------------------------------------
                BATCH OVERVIEW
                --------------------------------------------------------------
                Batch Count: {}
                Transaction Total Count In All Batches: {}
                Batch Total Amount: {}
                --------------------------------------------------------------
                PERFORMANCE RESULT
                --------------------------------------------------------------
                Produced Batch Count: {}
                Total Execution Time: {}
                --------------------------------------------------------------
                --------------------------------------------------------------
                """;

}
