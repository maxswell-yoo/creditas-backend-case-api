package com.github.maxswellyoo.creditas.unit.domain.strategy;

import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import com.github.maxswellyoo.creditas.domain.strategy.FixedPaymentCalculationStrategy;
import com.github.maxswellyoo.creditas.domain.strategy.PaymentCalculationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedPaymentCalculationStrategyTest {
    private final PaymentCalculationStrategy strategy = new FixedPaymentCalculationStrategy();

    @Test
    @DisplayName("Deve calcular a parcela mensal corretamente para entradas válidas")
    void testCalculateMonthlyPaymentValidInputs() {
        BigDecimal loanAmount = BigDecimal.valueOf(10000);
        int months = 12;
        InterestRateRule rule = () -> BigDecimal.valueOf(0.05);
        BigDecimal expectedPayment = new BigDecimal("856.07");

        BigDecimal result = strategy.calculateMonthlyPayment(loanAmount, rule, months);
        assertEquals(expectedPayment, result);
    }

    @Test
    @DisplayName("Deve retornar zero quando o valor do empréstimo é zero")
    void testCalculateMonthlyPaymentZeroLoan() {
        BigDecimal loanAmount = BigDecimal.ZERO;
        int months = 12;
        InterestRateRule rule = () -> BigDecimal.valueOf(0.05);

        BigDecimal result = strategy.calculateMonthlyPayment(loanAmount, rule, months);

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result);
    }

    @Test
    @DisplayName("Deve lançar ArithmeticException quando o número de meses é zero")
    void testCalculateMonthlyPaymentZeroMonths() {
        BigDecimal loanAmount = BigDecimal.valueOf(10000);
        int months = 0;
        InterestRateRule rule = () -> BigDecimal.valueOf(0.05);

        assertThrows(IllegalArgumentException.class, () -> strategy.calculateMonthlyPayment(loanAmount, rule, months));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o número de meses é negativo")
    void testCalculateMonthlyPaymentNegativeMonths() {
        BigDecimal loanAmount = BigDecimal.valueOf(10000);
        int months = -12;
        InterestRateRule rule = () -> BigDecimal.valueOf(0.05);

        assertThrows(IllegalArgumentException.class,
                () -> strategy.calculateMonthlyPayment(loanAmount, rule, months));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o valor do empréstimo é negativo")
    void testCalculateMonthlyPaymentNegativeLoanAmount() {
        BigDecimal loanAmount = BigDecimal.valueOf(-10000);
        int months = 12;
        InterestRateRule rule = () -> BigDecimal.valueOf(0.05);

        assertThrows(IllegalArgumentException.class, () -> strategy.calculateMonthlyPayment(loanAmount, rule, months));
    }
}
