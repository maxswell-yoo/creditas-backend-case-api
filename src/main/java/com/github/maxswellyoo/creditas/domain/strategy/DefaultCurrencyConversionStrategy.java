package com.github.maxswellyoo.creditas.domain.strategy;

import java.math.BigDecimal;

public class DefaultCurrencyConversionStrategy implements CurrencyConversionStrategy {
    @Override
    public BigDecimal convert(BigDecimal amount, BigDecimal conversionRate) {
        validateInputs(amount, conversionRate);
        return amount.multiply(conversionRate);
    }

    private void validateInputs(BigDecimal loanAmount, BigDecimal conversionRate) {
        if (loanAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor da conversão não pode ser negativo.");
        }
        if (conversionRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A taxa de conversão deve ser maior que zero.");
        }
    }
}
