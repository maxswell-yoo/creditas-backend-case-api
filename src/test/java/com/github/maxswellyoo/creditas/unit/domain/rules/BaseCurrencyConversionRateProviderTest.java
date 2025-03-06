package com.github.maxswellyoo.creditas.unit.domain.rules;


import com.github.maxswellyoo.creditas.domain.enums.Currency;
import com.github.maxswellyoo.creditas.domain.rules.BaseCurrencyConversionRateProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseCurrencyConversionRateProviderTest {
    @Test
    @DisplayName("Deve retornar taxa de conversão de 5,0 de USD para BRL")
    void testUsdToBrlConversion() {
        BigDecimal rate = BaseCurrencyConversionRateProvider.getConversionRate(Currency.USD, Currency.BRL);
        assertEquals(BigDecimal.valueOf(5.0), rate);
    }

    @Test
    @DisplayName("Deve retornar taxa de conversão de 6,0 para EUR para BRL")
    void testEurToBrlConversion() {
        BigDecimal rate = BaseCurrencyConversionRateProvider.getConversionRate(Currency.EUR, Currency.BRL);
        assertEquals(BigDecimal.valueOf(6.0), rate);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando a regra de conversão não for encontrada")
    void testConversionRateNotFound() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            BaseCurrencyConversionRateProvider.getConversionRate(Currency.BRL, Currency.USD);
        });
        String expectedMessage = "Nenhuma taxa de conversão encontrada para: " + Currency.BRL + " para " + Currency.USD;
        assertEquals(expectedMessage, exception.getMessage());
    }
}