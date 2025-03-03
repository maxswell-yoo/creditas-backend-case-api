package com.github.maxswellyoo.creditas.unit.domain.factory;

import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.factory.PaymentCalculationStrategyFactory;
import com.github.maxswellyoo.creditas.domain.strategy.FixedPaymentCalculationStrategy;
import com.github.maxswellyoo.creditas.domain.strategy.PaymentCalculationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCalculationStrategyFactoryTest {

    @Test
    @DisplayName("Deve retornar uma instância de FixedPaymentCalculationStrategy para CalculationType.FIXED")
    void testGetStrategyForFixed() {
        PaymentCalculationStrategy strategy = PaymentCalculationStrategyFactory.getStrategy(CalculationType.FIXED);
        assertNotNull(strategy);
        assertTrue(strategy instanceof FixedPaymentCalculationStrategy);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para um CalculationType não registrado")
    void testGetStrategyForUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> PaymentCalculationStrategyFactory.getStrategy(null));
    }
}
