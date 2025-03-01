package com.github.maxswellyoo.creditas.domain.rules;

import java.math.BigDecimal;

public interface InterestRateRule {
    BigDecimal getAnnualRate();
}
