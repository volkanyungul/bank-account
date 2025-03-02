package com.volkanyungul.bank_account.auditsystem.dto;

import lombok.Builder;

@Builder
public record AuditSubmission(Submission submission) {
}