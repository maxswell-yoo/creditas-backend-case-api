package com.github.maxswellyoo.creditas.domain.rules;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public final class BaseInterestRateProvider {
    private static final List<InterestRateRuleEntry> RULES = new ArrayList<>();

    static {
        RULES.add(new InterestRateRuleEntry(0, 25, BigDecimal.valueOf(0.05)));
        RULES.add(new InterestRateRuleEntry(26, 40, BigDecimal.valueOf(0.03)));
        RULES.add(new InterestRateRuleEntry(41, 60, BigDecimal.valueOf(0.02)));
        RULES.add(new InterestRateRuleEntry(61, Integer.MAX_VALUE, BigDecimal.valueOf(0.04)));
    }

    private BaseInterestRateProvider() {}

    public static BigDecimal getBaseInterestRate(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return RULES.stream()
                .filter(rule -> rule.applies(age))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhuma regra encontrada para a idade: " + age))
                .rate();
    }

    private record InterestRateRuleEntry(int minAge, int maxAge, BigDecimal rate) {

        public boolean applies(int age) {
                return age >= minAge && age <= maxAge;
            }
        }
}
