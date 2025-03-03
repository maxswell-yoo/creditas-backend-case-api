package com.github.maxswellyoo.creditas.infrastructure.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulateLoanRequest(
        @NotNull(message = "o campo loanAmount não pode ser nulo")
        @Positive(message = "o campo loanAmount deve conter um número positivo")
        BigDecimal loanAmount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        @NotNull(message = "o campo birthDate não pode ser nulo")
        @PastOrPresent(message = "o campo birthDate deve conter uma passada ou a data atual")
        LocalDate birthDate,
        @NotNull(message = "o campo months não pode ser vazio")
        @Positive(message = "o campo months deve conter um número inteiro positivo")
        Integer months
) implements Serializable {
}
