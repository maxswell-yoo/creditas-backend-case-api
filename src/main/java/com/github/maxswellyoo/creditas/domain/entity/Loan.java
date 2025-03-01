package com.github.maxswellyoo.creditas.domain.entity;

import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;
import com.github.maxswellyoo.creditas.domain.factory.InterestRateRuleFactory;
import com.github.maxswellyoo.creditas.domain.factory.PaymentCalculationStrategyFactory;
import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import com.github.maxswellyoo.creditas.domain.rules.InterestRateRuleProvider;
import com.github.maxswellyoo.creditas.domain.strategy.PaymentCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Loan {
    private final BigDecimal principal;
    private final LocalDate birthDate;
    private final int months;
    private final BigDecimal monthlyInstallment;
    private final BigDecimal totalAmount;
    private final BigDecimal totalInterest;

    public Loan(BigDecimal principal, LocalDate birthDate, int months,
                 BigDecimal monthlyInstallment, BigDecimal totalAmount, BigDecimal totalInterest) {
        this.principal = principal;
        this.birthDate = birthDate;
        this.months = months;
        this.monthlyInstallment = monthlyInstallment;
        this.totalAmount = totalAmount;
        this.totalInterest = totalInterest;
    }


    /**
     * Factory method que simula o empréstimo, utilizando regras de negócio do domínio.
     *
     * @param principal       Valor do empréstimo.
     * @param birthDate       Data de nascimento do cliente.
     * @param months          Número total de pagamentos.
     * @param scenario        Cenário de taxa de juros (FIXED ou VARIABLE).
     * @param calculationType Tipo de cálculo (por exemplo, "FIXED" ou "DECREASING").
     * @return Instância de Loan com os resultados da simulação.
     */
    public static Loan simulateLoan(BigDecimal principal,
                                    LocalDate birthDate,
                                    int months,
                                    InterestRateScenario scenario,
                                    CalculationType calculationType) {
        // Obtém a taxa base com base na idade
        BigDecimal baseRate = InterestRateRuleProvider.getInterestRate(birthDate);

        // Cria a regra de juros adequada utilizando a factory (FIXED ou VARIABLE)
        InterestRateRule rule = InterestRateRuleFactory.getRule(baseRate, scenario);

        // Seleciona a estratégia de cálculo de pagamento utilizando a factory
        PaymentCalculationStrategy strategy = PaymentCalculationStrategyFactory.getStrategy(calculationType);

        // Calcula a parcela mensal
        BigDecimal monthlyPayment = strategy.calculateMonthlyPayment(principal, rule, months);

        // Calcula o valor total a ser pago e os juros totais
        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(months));
        BigDecimal totalInterest = totalAmount.subtract(principal);

        return new Loan(principal, birthDate, months, monthlyPayment, totalAmount, totalInterest);
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public int getMonths() {
        return months;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }
}
