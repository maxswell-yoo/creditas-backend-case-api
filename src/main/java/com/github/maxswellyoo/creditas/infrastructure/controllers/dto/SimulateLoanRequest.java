package com.github.maxswellyoo.creditas.infrastructure.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulateLoanRequest(
        BigDecimal loanAmount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate birthDate,
        Integer months
) implements Serializable { }
