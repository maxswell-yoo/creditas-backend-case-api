package com.github.maxswellyoo.creditas.unit.domain.service;

import com.github.maxswellyoo.creditas.domain.enums.Currency;
import com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType;
import com.github.maxswellyoo.creditas.domain.factory.CurrencyConversionStrategyFactory;
import com.github.maxswellyoo.creditas.domain.rules.BaseCurrencyConversionRateProvider;
import com.github.maxswellyoo.creditas.domain.service.CurrencyConversionService;
import com.github.maxswellyoo.creditas.domain.strategy.CurrencyConversionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyConversionServiceTest {

    private BigDecimal amount;
    private Currency fromCurrency;
    private Currency targetCurrency;
    private CurrencyConversionType conversionType;
    private BigDecimal dummyRate;
    private BigDecimal expectedConvertedAmount;
    private CurrencyConversionStrategy dummyStrategy;

    @BeforeEach
    void setUp() {
        amount = BigDecimal.valueOf(100);
        fromCurrency = Currency.USD;
        targetCurrency = Currency.BRL;
        conversionType = CurrencyConversionType.DEFAULT;
        dummyRate = BigDecimal.valueOf(5.0);
        expectedConvertedAmount = BigDecimal.valueOf(500.0);
        dummyStrategy = mock(CurrencyConversionStrategy.class);
        when(dummyStrategy.convert(eq(amount), eq(dummyRate))).thenReturn(expectedConvertedAmount);
    }

    @Test
    @DisplayName("Deve chamar métodos de provedor e fábrica durante a conversão de moeda")
    void shouldCallProviderAndFactoryMethodsDuringCurrencyConversion() {
        try (MockedStatic<BaseCurrencyConversionRateProvider> providerMock = mockStatic(BaseCurrencyConversionRateProvider.class);
             MockedStatic<CurrencyConversionStrategyFactory> factoryMock = mockStatic(CurrencyConversionStrategyFactory.class)) {

            providerMock.when(() -> BaseCurrencyConversionRateProvider.getConversionRate(fromCurrency, targetCurrency))
                    .thenReturn(dummyRate);
            factoryMock.when(() -> CurrencyConversionStrategyFactory.getStrategy(conversionType))
                    .thenReturn(dummyStrategy);

            BigDecimal result = CurrencyConversionService.convertCurrency(amount, fromCurrency, targetCurrency, conversionType);

            providerMock.verify(() -> BaseCurrencyConversionRateProvider.getConversionRate(fromCurrency, targetCurrency), times(1));
            providerMock.verifyNoMoreInteractions();
            factoryMock.verify(() -> CurrencyConversionStrategyFactory.getStrategy(conversionType), times(1));
            factoryMock.verifyNoMoreInteractions();
            verify(dummyStrategy, times(1)).convert(amount, dummyRate);

            assertEquals(expectedConvertedAmount, result);
        }
    }

    @Test
    @DisplayName("Deve retornar o mesmo valor quando as moedas de origem e de destino forem idênticas")
    void shouldReturnSameAmountWhenSourceAndTargetCurrenciesAreIdentical() {
        BigDecimal result = CurrencyConversionService.convertCurrency(amount, Currency.USD, Currency.USD, conversionType);
        assertEquals(amount, result);
    }

    @Test
    @DisplayName("Deve converter USD para BRL usando uma taxa de conversão de 5,0")
    void shouldConvertUsdToBrlUsingConversionRateOfFive() {
        BigDecimal result = CurrencyConversionService.convertCurrency(amount, Currency.USD, Currency.BRL, conversionType);
        assertEquals(expectedConvertedAmount, result);
    }

    @Test
    @DisplayName("Deve converter EUR para BRL usando uma taxa de conversão de 6,0")
    void shouldConvertEurToBrlUsingConversionRateOfSix() {
        BigDecimal expected = BigDecimal.valueOf(600.0);
        BigDecimal result = CurrencyConversionService.convertCurrency(BigDecimal.valueOf(100), Currency.EUR, Currency.BRL, conversionType);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando nenhuma regra de conversão for encontrada")
    void shouldThrowExceptionWhenConversionRuleNotFound() {
        Exception exception = assertThrows(IllegalStateException.class, () ->
                CurrencyConversionService.convertCurrency(amount, Currency.BRL, Currency.USD, conversionType));
        String expectedMessage = "Nenhuma taxa de conversão encontrada para: " + Currency.BRL + " para " + Currency.USD;
        assertEquals(expectedMessage, exception.getMessage(), "The exception message should match the expected message");
    }
}
