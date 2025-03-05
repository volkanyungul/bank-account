# Bank Account App

We need to implement a solution that is able to track the balance of a bank account supporting credits, debits and balance enquiries. 
This system has an audit requirement where the last 1000 transactions need to be sent to a downstream audit system. 
This audit system requires transactions to be sent in batches however the total value of each batch is limited. 
We are charged for each batch sent therefore to save costs must minimize the number of batches sent. 

**The expectation is that the solution will have the following key components:**
* Producer which is responsible for generating transactions
* Balance Tracker which is responsible for:
  * Processing the transactions
  * Tracking the balance resulting from the transactions
  * Publishing batches of balances to an audit system

## Design Flows

![Image_Alt](https://github.com/volkanyungul/bank-account/blob/0639177129e241fc93e1043aead8104a0ca0a725/src/main/resources/docs/producerDesignFlow.png)

![Image_Alt](https://github.com/volkanyungul/bank-account/blob/0639177129e241fc93e1043aead8104a0ca0a725/src/main/resources/docs/balanceTrackerDesignFlow.png)

![Image_Alt](https://github.com/volkanyungul/bank-account/blob/0639177129e241fc93e1043aead8104a0ca0a725/src/main/resources/docs/auditSystemDesignFlow.png)

## Assumptions and implementation decisions
* My initial plan was to implement messaging from the **producer** to the **balance-tracker**, and from the **balance-tracker** to the **audit-system** using Kafka. In the BankAccountService code block, 
it states that processTransaction should be called by the credit and debit generation threads. Because of this, I thought that all modules should be part of the same application.
* In the 'Producer' section, it states: "Debits will have a negative amount and credits a positive amount.". My initial plan was to store the debit amount in the object as positive internally, 
and distinguish between **Credit** and **Debit** using the [TransactionType.java](src/main/java/com/volkanyungul/bank_account/producer/dto/TransactionType.java) enum. 
However, in the current implementation, amount is negative for Debit case. As a result, I only needed **"add"** method in the
[InMemoryBalanceRepository.java](src/main/java/com/volkanyungul/bank_account/balancetracker/repository/InMemoryBalanceRepository.java) to reflect the amount change.
* Since there is no significant DB requirements, I designed an in-memory repository and stored the balance in [InMemoryBalanceRepository.java](src/main/java/com/volkanyungul/bank_account/balancetracker/repository/InMemoryBalanceRepository.java)
* I implemented two batching algorithms [BatchOptimizedBatchProcessor.java](src/main/java/com/volkanyungul/bank_account/auditsystem/service/batchprocessor/BatchOptimizedBatchProcessor.java) and 
[TimeOptimizedBatchProcessor.java](src/main/java/com/volkanyungul/bank_account/auditsystem/service/batchprocessor/TimeOptimizedBatchProcessor.java) to prepare batches from transactions. However,
I selected *BatchOptimized* as the primary algorithm for the **batchAuditProcessorAlgorithm** configuration in application.yml. I set **BatchOptimized** as default because the requirements prioritized generating fewer batches over processing them quickly.

## Installation

```
mvn clean install
```

## Unit Testing

```
mvn test
```

## Integration Testing

```
mvn verify
``` 

Or, manually run [PerformanceIT.java](src/test/java/com/volkanyungul/bank_account/integration/performance/PerformanceIT.java) 
or [BankAccountEndToEndIT.java](src/test/java/com/volkanyungul/bank_account/integration/BankAccountEndToEndIT.java)

## How Integration Test Works? 
I disabled [TransactionSchedulerFactory.java](src/main/java/com/volkanyungul/bank_account/producer/scheduler/TransactionSchedulerFactory.java) class by giving **@Profile("!test")** since its non-stop generating 
transactions. I am loading these beans in [BankAccountEndToEndITConfig.java](src/test/java/com/volkanyungul/bank_account/integration/BankAccountEndToEndITConfig.java) manually. When all the context beans are loaded,
contextRefreshedListener method gets triggered in BankAccountEndToEndITConfig.java[BankAccountEndToEndITConfig.java](src/test/java/com/volkanyungul/bank_account/integration/BankAccountEndToEndITConfig.java) and schedules the two threads.
**latch.await()** lets us block the test thread on the latch, and let the transaction traffic run over the system.

When the batch files are ready to submit, [AbstractBatchProcessor.java](src/main/java/com/volkanyungul/bank_account/auditsystem/service/batchprocessor/AbstractBatchProcessor.java) sends an AuditReadyEvent[AuditReadyEvent.java](src/main/java/com/volkanyungul/bank_account/events/AuditReadyEvent.java)
which is triggering the onApplicationEvent method in BankAccountEndToEndITConfig.java and it shut down the transaction producer threads. Then it count down the latch and let the test framework continue it's test and make the validations. 

## Performance Testing
```
Run PerformanceIT.java test class to see the performance output
```

To run the performance test with a different algorithm, update **batchAuditProcessorAlgorithm** config in application-test.yml file with one of the options: **[BatchOptimized, TimeOptimized]**. When **'BatchOptimized'** is selected, [BatchOptimizedBatchProcessor.java](src/main/java/com/volkanyungul/bank_account/auditsystem/service/batchprocessor/BatchOptimizedBatchProcessor.java)
will be loaded and it will handle the batching operation. If **'TimeOptimized'** is selected,
[TimeOptimizedBatchProcessor.java](src/main/java/com/volkanyungul/bank_account/auditsystem/service/batchprocessor/TimeOptimizedBatchProcessor.java) will work and split the transactions
into batches.
```
performance:
  batchAuditProcessorAlgorithm: BatchOptimized
```

## Performance Comparison
|                                                              | **BatchOptimized** | **TimeOptimized** | 
|--------------------------------------------------------------|--------------------|-------------------|
| Thread Count                                                 | 2                  | 2                 |
| Thread Running Period (in seconds)                           | 1                  | 1                 |
| Credit Transaction Count                                     | 1000               | 1000              |
| Debit Transaction Count                                      | 1000               | 1000              | 
| Transaction Count Threshold                                  | 100.000            | 100.000           |
| Transaction Total Value Of All Transactions Threshold (in £) | 1.000.000          | 1.000.000         | 
|                                                              |                    |                   | 
| Produced Batch Count                                         | **25017**          | **28981**         |
| Total Execution Time In Seconds                              | **63.760308896**   | **51.212714899**  |
  
## Debugging
Change the log level into DEBUG in logback.xml. Messages like 'Transaction Created', 'Transaction Processed', etc. will be logged in DEBUG level.
```
<logger name="com.volkanyungul.bank_account" level="INFO" />
```

## References
* [Application Event Publisher](https://www.baeldung.com/spring-events)
* [Atomic Reference for BigDecimal](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicReference.html#updateAndGet-java.util.function.UnaryOperator-)
* [Thread Local Random](https://www.baeldung.com/java-thread-local-random)
* [ReEntrant Lock](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html)
* [Converting object to Json](https://stackoverflow.com/questions/15786129/converting-java-objects-to-json-with-jackson)

## Test References
* [Testing with Awaitility](https://stackoverflow.com/questions/71346616/how-can-i-await-at-least-specified-amount-of-time-with-awaitility) and [Awaitility](https://www.javadoc.io/doc/org.awaitility/awaitility/2.0.0/index.html)
* [Testing Web](https://spring.io/guides/gs/testing-web)
* [Console Logging Test](https://stackoverflow.com/questions/64880276/how-to-capture-logs-with-logcaptor-or-mockito)

## Built With

* [The Java™](https://docs.oracle.com/javase/tutorial/) - Java 21
* [Spring Framework](https://spring.io/projects/spring-framework) - Application Framework
* [Maven](https://maven.apache.org/) - Dependency Management
* [Logback](https://logback.qos.ch/) - Logging
* [Junit5](https://junit.org/junit5/) - Unit Testing
* [Mockito](https://site.mockito.org/) - Mocking
* [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/) - Integration testing
* [Lombok](https://projectlombok.org/)