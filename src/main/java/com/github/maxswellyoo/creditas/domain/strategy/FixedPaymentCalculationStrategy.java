package com.github.maxswellyoo.creditas.domain.strategy;

import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class FixedPaymentCalculationStrategy implements PaymentCalculationStrategy {

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, InterestRateRule rule, int months) {
        validateInputs(loanAmount, months);
        MathContext mathContext = MathContext.DECIMAL128;

        BigDecimal annualRate = rule.getAnnualRate();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), mathContext);

        BigDecimal numerator = loanAmount.multiply(monthlyRate, mathContext);

        BigDecimal factor = BigDecimal.ONE.add(monthlyRate, mathContext).pow(-months, mathContext);
        BigDecimal denominator = BigDecimal.ONE.subtract(factor, mathContext);

        BigDecimal payment = numerator.divide(denominator, mathContext);
        return payment.setScale(2, RoundingMode.HALF_EVEN);
    }

    private void validateInputs(BigDecimal loanAmount, int months) {
        if (loanAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor do empréstimo não pode ser negativo.");
        }
        if (months <= 0) {
            throw new IllegalArgumentException("O número de meses deve ser maior que zero.");
        }
    }

}
