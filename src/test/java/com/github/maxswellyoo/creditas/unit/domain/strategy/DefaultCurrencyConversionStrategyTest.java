package com.github.maxswellyoo.creditas.unit.domain.strategy;

import com.github.maxswellyoo.creditas.domain.strategy.CurrencyConversionStrategy;
import com.github.maxswellyoo.creditas.domain.strategy.DefaultCurrencyConversionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultCurrencyConversionStrategyTest {
    private final CurrencyConversionStrategy strategy;

    DefaultCurrencyConversionStrategyTest() {
        strategy = new DefaultCurrencyConversionStrategy();
    }

    @Test
    @DisplayName("Deve converter o valor corretamente quando entradas válidas são fornecidas")
    void testConvertValidInputs() {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal conversionRate = BigDecimal.valueOf(5.0);
        BigDecimal expected = BigDecimal.valueOf(500.0);

        BigDecimal result = strategy.convert(amount, conversionRate);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o valor for negativo")
    void testConvertNegativeAmount() {
        BigDecimal amount = BigDecimal.valueOf(-100);
        BigDecimal conversionRate = BigDecimal.valueOf(5.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.convert(amount, conversionRate);
        });
        String expectedMessage = "O valor da conversão não pode ser negativo.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando ConversionRate for negativo")
    void testConvertNegativeConversionRate() {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal conversionRate = BigDecimal.valueOf(-5.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.convert(amount, conversionRate);
        });
        String expectedMessage = "A taxa de conversão deve ser maior que zero.";
        assertEquals(expectedMessage, exception.getMessage());
    }
}