package com.github.maxswellyoo.creditas.domain.rules;

import java.math.BigDecimal;

public final class FixedInterestRateRule implements InterestRateRule {
    private final BigDecimal annualRate;

    public FixedInterestRateRule(BigDecimal annualRate) {
        this.annualRate = annualRate;
    }

    @Override
    public BigDecimal getAnnualRate() {
        return this.annualRate;
    }
}
