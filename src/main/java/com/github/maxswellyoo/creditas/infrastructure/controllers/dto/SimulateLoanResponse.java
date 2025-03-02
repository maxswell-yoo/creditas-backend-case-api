package com.github.maxswellyoo.creditas.infrastructure.controllers.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulateLoanResponse(
        BigDecimal totalAmount,
        BigDecimal monthlyInstallment,
        Integer months,
        BigDecimal totalInterest
) implements Serializable { }
