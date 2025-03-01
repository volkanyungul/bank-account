package com.volkanyungul.bank_account.producer.config;

import com.volkanyungul.bank_account.producer.dto.Range;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix="producer")
public class ProducerProperties {
    private int transactionCountPerSecond;
    private Range transactionAmountRange;
}
