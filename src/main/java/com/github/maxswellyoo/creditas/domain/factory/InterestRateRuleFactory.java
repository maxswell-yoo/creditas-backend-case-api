package com.github.maxswellyoo.creditas.domain.factory;

import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;
import com.github.maxswellyoo.creditas.domain.rules.FixedInterestRateRule;
import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;

import java.math.BigDecimal;

public final class InterestRateRuleFactory {
    private InterestRateRuleFactory() {}

    public static InterestRateRule getRule(BigDecimal rate, InterestRateScenario scenario) {
        if (scenario == null) {
            throw new IllegalArgumentException("Cenário de taxa de juros não pode ser nulo.");
        }

        switch (scenario) {
            case FIXED:
                return new FixedInterestRateRule(rate);
            default:
                throw new IllegalArgumentException("Cenário de taxa de juros não suportado: " + scenario);
        }
    }
}
