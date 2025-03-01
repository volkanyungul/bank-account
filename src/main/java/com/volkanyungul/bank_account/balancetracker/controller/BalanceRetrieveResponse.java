package com.volkanyungul.bank_account.balancetracker.controller;

import lombok.Builder;

@Builder
public record BalanceRetrieveResponse(double balance) {
}
