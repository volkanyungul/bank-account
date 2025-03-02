package com.volkanyungul.bank_account.auditsystem.service.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsoleAuditSubmitter implements AuditSubmitter {

    @Override
    public void submit(AuditSubmission auditSubmission) {
        try {
            log.info(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(auditSubmission));
        } catch (Exception e) {
            log.error("Error happened while printing submission", e);
        }
    }
}
