package com.github.maxswellyoo.creditas.domain.entity;

import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;
import com.github.maxswellyoo.creditas.domain.factory.InterestRateRuleFactory;
import com.github.maxswellyoo.creditas.domain.factory.PaymentCalculationStrategyFactory;
import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import com.github.maxswellyoo.creditas.domain.rules.BaseInterestRateProvider;
import com.github.maxswellyoo.creditas.domain.strategy.PaymentCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Loan {
    private final BigDecimal loanAmount;
    private final LocalDate birthDate;
    private final int months;
    private final BigDecimal monthlyInstallment;
    private final BigDecimal totalAmount;
    private final BigDecimal totalInterest;
    private final String email;

    public Loan(BigDecimal loanAmount, LocalDate birthDate, int months,
                 BigDecimal monthlyInstallment, BigDecimal totalAmount, BigDecimal totalInterest, String email) {
        this.loanAmount = loanAmount;
        this.birthDate = birthDate;
        this.months = months;
        this.monthlyInstallment = monthlyInstallment;
        this.totalAmount = totalAmount;
        this.totalInterest = totalInterest;
        this.email = email;
    }

    /**
     * Factory method que simula o empréstimo, utilizando regras de negócio do domínio.
     *
     * @param loanAmount       Valor do empréstimo.
     * @param birthDate       Data de nascimento do cliente.
     * @param months          Número total de pagamentos.
     * @param email           Email do cliente do empréstimo
     * @param scenario        Cenário de taxa de juros (FIXED ou VARIABLE).
     * @param calculationType Tipo de cálculo (por exemplo, "FIXED" ou "DECREASING").
     * @return Instância de Loan com os resultados da simulação.
     */
    public static Loan simulateLoan(BigDecimal loanAmount,
                                    LocalDate birthDate,
                                    int months,
                                    String email,
                                    InterestRateScenario scenario,
                                    CalculationType calculationType) {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
        }

        // Obtém a taxa base com base na idade
        BigDecimal baseAnnualRate = BaseInterestRateProvider.getBaseInterestRate(birthDate);

        // Cria a regra de juros adequada utilizando a factory (FIXED ou VARIABLE)
        InterestRateRule rule = InterestRateRuleFactory.getRule(baseAnnualRate, scenario);

        // Seleciona a estratégia de cálculo de pagamento utilizando a factory
        PaymentCalculationStrategy strategy = PaymentCalculationStrategyFactory.getStrategy(calculationType);

        // Calcula a parcela mensal
        BigDecimal monthlyPayment = strategy.calculateMonthlyPayment(loanAmount, rule, months);

        // Calcula o valor total a ser pago e os juros totais
        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(months));
        BigDecimal totalInterest = totalAmount.subtract(loanAmount);

        return new Loan(loanAmount, birthDate, months, monthlyPayment, totalAmount, totalInterest, email);
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
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
