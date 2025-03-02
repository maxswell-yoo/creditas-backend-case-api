package com.github.maxswellyoo.creditas.domain.strategy;

import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class FixedPaymentCalculationStrategy implements PaymentCalculationStrategy {

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, InterestRateRule rule, int months) {
        BigDecimal annualRate = rule.getAnnualRate();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP);

        BigDecimal numerator = loanAmount.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.add(monthlyRate).pow(-months, MathContext.DECIMAL128));

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
