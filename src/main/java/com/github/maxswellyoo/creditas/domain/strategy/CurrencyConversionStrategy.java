package com.github.maxswellyoo.creditas.domain.strategy;

import java.math.BigDecimal;

public interface CurrencyConversionStrategy {
    BigDecimal convert(BigDecimal amount, BigDecimal conversionRate);
}
