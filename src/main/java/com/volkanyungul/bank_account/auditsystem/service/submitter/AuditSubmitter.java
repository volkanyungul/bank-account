package com.volkanyungul.bank_account.auditsystem.service.submitter;

import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;

public interface AuditSubmitter {
    void submit(AuditSubmission auditSubmission);
}
