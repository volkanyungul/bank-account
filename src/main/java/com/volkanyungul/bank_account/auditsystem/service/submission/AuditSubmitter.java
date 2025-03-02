package com.volkanyungul.bank_account.auditsystem.service.submission;

import com.volkanyungul.bank_account.auditsystem.dto.AuditSubmission;

public interface AuditSubmitter {
    void submit(AuditSubmission auditSubmission);
}
