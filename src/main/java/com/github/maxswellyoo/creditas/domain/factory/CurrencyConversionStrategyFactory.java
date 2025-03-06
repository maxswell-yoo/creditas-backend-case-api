package com.github.maxswellyoo.creditas.domain.factory;

import com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType;
import com.github.maxswellyoo.creditas.domain.strategy.CurrencyConversionStrategy;
import com.github.maxswellyoo.creditas.domain.strategy.DefaultCurrencyConversionStrategy;

import java.util.HashMap;
import java.util.Map;

import static com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType.DEFAULT;

public class CurrencyConversionStrategyFactory {
    private static final Map<CurrencyConversionType, CurrencyConversionStrategy> STRATEGIES = new HashMap<>();

    static {
        registerStrategy(DEFAULT, new DefaultCurrencyConversionStrategy());
    }

    private static void registerStrategy(CurrencyConversionType type, CurrencyConversionStrategy strategy) {
        STRATEGIES.put(type, strategy);
    }

    /**
     * Retorna a estratégia correspondente ao tipo informado.
     *
     * @param type Tipo da conversão desejado.
     * @return Implementação de CurrencyConversionStrategy.
     */
    public static CurrencyConversionStrategy getStrategy(CurrencyConversionType type) {
        if (!STRATEGIES.containsKey(type)) {
            throw new IllegalArgumentException("Nenhuma estratégia encontrada para o tipo: " + type);
        }
        return STRATEGIES.get(type);
    }
}
