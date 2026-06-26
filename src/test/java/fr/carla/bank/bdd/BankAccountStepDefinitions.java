package fr.carla.bank.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.carla.bank.dto.AmountRequest;
import fr.carla.bank.dto.CreateAccountRequest;
import fr.carla.bank.dto.TransferRequest;
import fr.carla.bank.repository.BankAccountRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class BankAccountStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankAccountRepository repository;

    private MvcResult lastResult;

    @Before
    public void setUp() {
        repository.clear();
        lastResult = null;
    }

    @Given("the bank contains no accounts")
    public void theBankContainsNoAccounts() {
        repository.clear();
    }

    @Given("account {string} exists for holder {string} with balance {double}")
    public void accountExistsWithBalance(
            String number,
            String holder,
            double balance
    ) throws Exception {
        createAccount(number, holder);

        if (balance > 0) {
            deposit(number, BigDecimal.valueOf(balance));
        }
    }

    @When("a customer creates account {string} for holder {string}")
    public void aCustomerCreatesAccount(
            String number,
            String holder
    ) throws Exception {
        CreateAccountRequest request =
                new CreateAccountRequest(number, holder);

        lastResult = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @When("{double} is deposited into account {string}")
    public void amountIsDeposited(double amount, String number)
            throws Exception {
        lastResult = deposit(
                number,
                BigDecimal.valueOf(amount)
        );
    }

    @When("{double} is withdrawn from account {string}")
    public void amountIsWithdrawn(double amount, String number)
            throws Exception {
        AmountRequest request =
                new AmountRequest(BigDecimal.valueOf(amount));

        lastResult = mockMvc.perform(
                        post("/api/accounts/" + number + "/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andReturn();
    }

    @When("{double} is transferred from {string} to {string}")
    public void amountIsTransferred(
            double amount,
            String source,
            String target
    ) throws Exception {
        TransferRequest request = new TransferRequest(
                source,
                target,
                BigDecimal.valueOf(amount)
        );

        lastResult = mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Then("account {string} is created")
    public void accountIsCreated(String number) throws Exception {
        assertEquals(201, lastResult.getResponse().getStatus());

        MvcResult result = mockMvc.perform(
                        get("/api/accounts/" + number)
                )
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Then("account {string} has a balance of {double}")
    public void accountHasBalance(
            String number,
            double expectedBalance
    ) throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/accounts/" + number)
                )
                .andReturn();

        BigDecimal actualBalance = objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("balance")
                .decimalValue();

        assertEquals(
                0,
                BigDecimal.valueOf(expectedBalance)
                        .compareTo(actualBalance)
        );
    }

    @Then("a conflict response is returned")
    public void aConflictResponseIsReturned() {
        assertEquals(409, lastResult.getResponse().getStatus());
    }

    private void createAccount(
            String number,
            String holder
    ) throws Exception {
        CreateAccountRequest request =
                new CreateAccountRequest(number, holder);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    private MvcResult deposit(
            String number,
            BigDecimal amount
    ) throws Exception {
        AmountRequest request = new AmountRequest(amount);

        return mockMvc.perform(
                        post("/api/accounts/" + number + "/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andReturn();
    }
}