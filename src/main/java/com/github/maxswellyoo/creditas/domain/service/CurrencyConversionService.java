package com.github.maxswellyoo.creditas.domain.service;

import com.github.maxswellyoo.creditas.domain.enums.Currency;
import com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType;
import com.github.maxswellyoo.creditas.domain.factory.CurrencyConversionStrategyFactory;
import com.github.maxswellyoo.creditas.domain.rules.BaseCurrencyConversionRateProvider;
import com.github.maxswellyoo.creditas.domain.strategy.CurrencyConversionStrategy;

import java.math.BigDecimal;

public final class CurrencyConversionService {

    /**
     *
     * @param amount valor a ser convertido
     * @param fromCurrency moeda a qual vai ser convertida
     * @param targetCurrency moeda alvo a ser convertida
     * @param currencyConversionType tipo da conversão (DEFAULT)
     * @return
     */
    public static BigDecimal convertCurrency(BigDecimal amount,
                                             Currency fromCurrency,
                                             Currency targetCurrency,
                                             CurrencyConversionType currencyConversionType) {
        if (fromCurrency.equals(targetCurrency)) {
            return amount;
        }
        // Converte o total para a moeda desejada
        BigDecimal conversionRate = BaseCurrencyConversionRateProvider.getConversionRate(fromCurrency, targetCurrency);
        CurrencyConversionStrategy conversionStrategy = CurrencyConversionStrategyFactory.getStrategy(currencyConversionType);
        return conversionStrategy.convert(amount, conversionRate);
    }
}
