package com.volkanyungul.bank_account.producer.config;

import com.volkanyungul.bank_account.producer.dto.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix="producer")
public class ProducerProperties {
    private Map<TransactionType, ProducerTransactionConfig> transactionTypeConfigMap;
    private ProducerSchedulerConfig scheduler;
}
