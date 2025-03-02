package com.volkanyungul.bank_account.auditsystem.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record Submission(List<Batch> batches) {
}
