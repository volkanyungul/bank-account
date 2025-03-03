package com.volkanyungul.bank_account.balancetracker.controller;

import com.volkanyungul.bank_account.balancetracker.service.BankAccountService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BankAccountController.class)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankAccountService bankAccountService;

    @Test
    @SneakyThrows
    void shouldReturnBalance() {
        // given
        when(bankAccountService.retrieveBalance()).thenReturn(1234.56d);
        // when
        // then
        this.mockMvc.perform(get("/v1/bank-account/balance"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"balance\":1234.56}"));
    }
}