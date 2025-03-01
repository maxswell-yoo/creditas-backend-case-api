package com.github.maxswellyoo.creditas.domain.factory;

import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.strategy.FixedPaymentCalculationStrategy;
import com.github.maxswellyoo.creditas.domain.strategy.PaymentCalculationStrategy;

import java.util.HashMap;
import java.util.Map;

import static com.github.maxswellyoo.creditas.domain.enums.CalculationType.FIXED;

public final class PaymentCalculationStrategyFactory {
    private static final Map<CalculationType, PaymentCalculationStrategy> STRATEGIES = new HashMap<>();

    static {
        registerStrategy(FIXED, new FixedPaymentCalculationStrategy());
    }

    private static void registerStrategy(CalculationType type, PaymentCalculationStrategy strategy) {
        STRATEGIES.put(type, strategy);
    }

    /**
     * Retorna a estratégia correspondente ao tipo informado.
     *
     * @param type Tipo de cálculo desejado.
     * @return Implementação de PaymentCalculationStrategy.
     */
    public static PaymentCalculationStrategy getStrategy(CalculationType type) {
        if (!STRATEGIES.containsKey(type)) {
            throw new IllegalArgumentException("Nenhuma estratégia encontrada para o tipo: " + type);
        }
        return STRATEGIES.get(type);
    }

}
