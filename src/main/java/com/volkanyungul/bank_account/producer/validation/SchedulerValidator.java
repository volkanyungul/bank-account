package com.volkanyungul.bank_account.producer.validation;

import com.volkanyungul.bank_account.producer.config.ProducerProperties;
import com.volkanyungul.bank_account.producer.config.ProducerSchedulerConfig;
import com.volkanyungul.bank_account.producer.exception.SchedulerPeriodNotValidException;
import org.springframework.stereotype.Component;

@Component
public class SchedulerValidator implements ProducerValidator<ProducerProperties> {

    private static final String ERR_MSG_PERIOD_IN_SECONDS_NOT_VALID = "Scheduler period in seconds option should be bigger than zero";

    @Override
    public void validate(ProducerProperties producerProperties) {
        ProducerSchedulerConfig scheduler = producerProperties.getScheduler();

        if(scheduler.getPeriodInSeconds() < 0) {
            throw new SchedulerPeriodNotValidException(ERR_MSG_PERIOD_IN_SECONDS_NOT_VALID);
        }
    }
}
