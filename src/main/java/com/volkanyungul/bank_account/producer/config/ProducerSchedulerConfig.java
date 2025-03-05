package com.volkanyungul.bank_account.producer.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Builder
@ConfigurationProperties(prefix="scheduler")
public class ProducerSchedulerConfig {
    private Long initialDelay;
    private Long periodInSeconds;
    private boolean enabled;
}
