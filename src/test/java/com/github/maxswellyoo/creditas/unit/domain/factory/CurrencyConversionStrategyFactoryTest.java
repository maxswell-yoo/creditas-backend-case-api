package com.github.maxswellyoo.creditas.unit.domain.factory;


import com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType;
import com.github.maxswellyoo.creditas.domain.factory.CurrencyConversionStrategyFactory;
import com.github.maxswellyoo.creditas.domain.strategy.CurrencyConversionStrategy;
import com.github.maxswellyoo.creditas.domain.strategy.DefaultCurrencyConversionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConversionStrategyFactoryTest {
    @Test
    @DisplayName("Deve retornar DefaultCurrencyConversionStrategy quando o tipo for DEFAULT")
    void testGetStrategyWithDefault() {
        CurrencyConversionStrategy strategy = CurrencyConversionStrategyFactory.getStrategy(CurrencyConversionType.DEFAULT);
        assertNotNull(strategy);
        assertInstanceOf(DefaultCurrencyConversionStrategy.class, strategy);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o tipo não for suportado (nulo)")
    void testGetStrategyWithUnsupportedType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CurrencyConversionStrategyFactory.getStrategy(null);
        });
        assertEquals("Nenhuma estratégia encontrada para o tipo: null", exception.getMessage());
    }
}