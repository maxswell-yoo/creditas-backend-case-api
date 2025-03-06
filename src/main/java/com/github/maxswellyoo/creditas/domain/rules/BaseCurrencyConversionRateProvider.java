package com.github.maxswellyoo.creditas.domain.rules;

import com.github.maxswellyoo.creditas.domain.enums.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class BaseCurrencyConversionRateProvider {
    private static final List<CurrencyConversionRuleEntry> RULES = new ArrayList<>();

    static {
        RULES.add(new CurrencyConversionRuleEntry(Currency.USD, Currency.BRL, BigDecimal.valueOf(5.0)));
        RULES.add(new CurrencyConversionRuleEntry(Currency.EUR, Currency.BRL, BigDecimal.valueOf(6.0)));
    }

    private BaseCurrencyConversionRateProvider() {}

    public static BigDecimal getConversionRate(Currency from, Currency to) {
        return RULES.stream()
                .filter(rule -> rule.applies(from, to))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhuma taxa de conversão encontrada para: " + from + " para " + to))
                .rate();
    }

    private record CurrencyConversionRuleEntry(Currency from, Currency to, BigDecimal rate) {
        public boolean applies(Currency from, Currency to) {
            return this.from == from && this.to == to;
        }
    }

}
