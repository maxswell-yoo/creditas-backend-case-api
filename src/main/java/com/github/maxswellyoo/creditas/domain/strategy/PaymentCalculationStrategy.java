package com.github.maxswellyoo.creditas.domain.strategy;

import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import java.math.BigDecimal;

public interface PaymentCalculationStrategy {
    BigDecimal calculateMonthlyPayment(BigDecimal principal, InterestRateRule rule, int months);
}
