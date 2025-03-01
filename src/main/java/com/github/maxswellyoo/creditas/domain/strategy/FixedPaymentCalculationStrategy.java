package com.github.maxswellyoo.creditas.domain.strategy;

import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class FixedPaymentCalculationStrategy implements PaymentCalculationStrategy {

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal principal, InterestRateRule rule, int months) {
        BigDecimal annualRate = rule.getAnnualRate();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal numerator = principal.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.add(monthlyRate).pow(-months, MathContext.DECIMAL64));

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
