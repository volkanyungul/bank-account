package com.volkanyungul.bank_account.auditsystem.service.submitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsoleAuditSubmitter implements AuditSubmitter {

    private final ObjectMapper objectMapper;

    public ConsoleAuditSubmitter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void submit(AuditSubmission auditSubmission) {
        try {
            log.info(objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(auditSubmission));
        } catch (Exception e) {
            log.error("Error happened while printing submission", e);
        }
    }
}
