package com.volkanyungul.bank_account.balancetracker.controller;

import com.volkanyungul.bank_account.balancetracker.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bank-account")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping("/balance")
    public ResponseEntity<BalanceRetrieveResponse> getBalance() {
        double balance = bankAccountService.retrieveBalance();
        return ResponseEntity.ok(BalanceRetrieveResponse.builder().balance(balance).build());
    }
}
