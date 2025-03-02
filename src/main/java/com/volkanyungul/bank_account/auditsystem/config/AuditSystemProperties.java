package com.volkanyungul.bank_account.auditsystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "audit-system")
public class AuditSystemProperties {

    private long transactionCountThreshold;
    private BigDecimal totalValueOfAllTransactionsThreshold;
}
