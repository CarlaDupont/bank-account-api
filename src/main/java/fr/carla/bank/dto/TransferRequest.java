package fr.carla.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(

        @NotBlank(message = "Source account is required")
        String fromNumber,

        @NotBlank(message = "Target account is required")
        String toNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be strictly positive"
        )
        BigDecimal amount
) {
}