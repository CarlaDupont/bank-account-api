package fr.carla.bank.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.carla.bank.dto.AmountRequest;
import fr.carla.bank.dto.CreateAccountRequest;
import fr.carla.bank.dto.TransferRequest;
import fr.carla.bank.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BankAccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankAccountRepository repository;

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void shouldCreateDepositTransferAndReadAccounts() throws Exception {

        CreateAccountRequest firstAccountRequest =
                new CreateAccountRequest("ACC001", "Carla");

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("ACC001"))
                .andExpect(jsonPath("$.holder").value("Carla"))
                .andExpect(jsonPath("$.balance").value(0));

        CreateAccountRequest secondAccountRequest =
                new CreateAccountRequest("ACC002", "Alice");

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("ACC002"))
                .andExpect(jsonPath("$.balance").value(0));

        AmountRequest depositRequest =
                new AmountRequest(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/accounts/ACC001/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.00));

        TransferRequest transferRequest =
                new TransferRequest(
                        "ACC001",
                        "ACC002",
                        new BigDecimal("30.00")
                );

        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/accounts/ACC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(70.00));

        mockMvc.perform(get("/api/accounts/ACC002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(30.00));
    }
}