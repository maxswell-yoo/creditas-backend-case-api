package com.github.maxswellyoo.creditas.unit.domain.factory;

import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;
import com.github.maxswellyoo.creditas.domain.factory.InterestRateRuleFactory;
import com.github.maxswellyoo.creditas.domain.rules.FixedInterestRateRule;
import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InterestRateRuleFactoryTest {

    @Test
    @DisplayName("Deve retornar uma instância de FixedInterestRateRule para o cenário FIXED")
    void testGetRuleForFixedScenario() {
        BigDecimal rate = BigDecimal.valueOf(0.05);
        InterestRateRule rule = InterestRateRuleFactory.getRule(rate, InterestRateScenario.FIXED);

        assertNotNull(rule);
        assertInstanceOf(FixedInterestRateRule.class, rule);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para cenário não suportado")
    void testGetRuleForUnsupportedScenario() {
        BigDecimal rate = BigDecimal.valueOf(0.05);
        assertThrows(IllegalArgumentException.class, () -> InterestRateRuleFactory.getRule(rate, null));
    }
}