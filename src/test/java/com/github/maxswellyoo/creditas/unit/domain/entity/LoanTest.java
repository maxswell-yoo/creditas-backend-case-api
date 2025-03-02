package com.github.maxswellyoo.creditas.unit.domain.entity;

import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;
import com.github.maxswellyoo.creditas.domain.factory.InterestRateRuleFactory;
import com.github.maxswellyoo.creditas.domain.factory.PaymentCalculationStrategyFactory;
import com.github.maxswellyoo.creditas.domain.rules.InterestRateRule;
import com.github.maxswellyoo.creditas.domain.rules.InterestRateRuleProvider;
import com.github.maxswellyoo.creditas.domain.strategy.PaymentCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTest {
    private BigDecimal loanAmount;
    private LocalDate birthDate;
    private int months;
    private InterestRateScenario scenario;
    private CalculationType calculationType;

    // Valores dummy para mocks
    private BigDecimal dummyBaseRate;
    private InterestRateRule dummyRule;
    private BigDecimal dummyMonthlyPayment;
    private PaymentCalculationStrategy dummyStrategy;

    @BeforeEach
    void setUp() {
        loanAmount = BigDecimal.valueOf(10000);
        birthDate = LocalDate.of(2004, 11, 2);
        months = 12;
        scenario = InterestRateScenario.FIXED;
        calculationType = CalculationType.FIXED;

        dummyBaseRate = BigDecimal.valueOf(0.05);
        dummyRule = () -> dummyBaseRate;
        dummyMonthlyPayment = BigDecimal.valueOf(856.07);
        dummyStrategy = mock(PaymentCalculationStrategy.class);
    }

    @Test
    @DisplayName("Deve invocar os métodos internos de simulação corretamente")
    void testSimulateLoanInternalCalls() {
        try (MockedStatic<InterestRateRuleProvider> ruleProviderMock = mockStatic(InterestRateRuleProvider.class);
             MockedStatic<InterestRateRuleFactory> ruleFactoryMock = mockStatic(InterestRateRuleFactory.class);
             MockedStatic<PaymentCalculationStrategyFactory> strategyFactoryMock = mockStatic(PaymentCalculationStrategyFactory.class);
             MockedStatic<Loan> loanStaticMock = mockStatic(Loan.class)) {

            // Configura os mocks dos métodos estáticos
            ruleProviderMock.when(() -> InterestRateRuleProvider.getInterestRate(birthDate))
                    .thenReturn(dummyBaseRate);
            ruleFactoryMock.when(() -> InterestRateRuleFactory.getRule(dummyBaseRate, scenario))
                    .thenReturn(dummyRule);
            strategyFactoryMock.when(() -> PaymentCalculationStrategyFactory.getStrategy(calculationType))
                    .thenReturn(dummyStrategy);
            when(dummyStrategy.calculateMonthlyPayment(loanAmount, dummyRule, months))
                    .thenReturn(dummyMonthlyPayment);


            loanStaticMock.when(() -> Loan.simulateLoan(loanAmount, birthDate, months, scenario, calculationType))
                    .thenCallRealMethod();
            Loan result = Loan.simulateLoan(loanAmount, birthDate, months, scenario, calculationType);

            assertNotNull(result);
            assertEquals(dummyMonthlyPayment, result.getMonthlyInstallment());

            // verifica chamadas dos mocks
            verify(dummyStrategy, times(1)).calculateMonthlyPayment(loanAmount, dummyRule, months);

            ruleProviderMock.verify(() ->
                    InterestRateRuleProvider.getInterestRate(birthDate), times(1));
            ruleFactoryMock.verify(() ->
                    InterestRateRuleFactory.getRule(dummyBaseRate, scenario), times(1));
            strategyFactoryMock.verify(() ->
                    PaymentCalculationStrategyFactory.getStrategy(calculationType), times(1));
            loanStaticMock.verify(() ->
                            Loan.simulateLoan(loanAmount, birthDate, months, scenario, calculationType),
                    times(1));
        }
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para valor de empréstimo negativo")
    void testSimulateLoanNegativeLoanAmount() {
        BigDecimal negativeLoanAmount = BigDecimal.valueOf(-10000);
        assertThrows(IllegalArgumentException.class, () ->
                Loan.simulateLoan(negativeLoanAmount, birthDate, months, scenario, calculationType));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para número de meses inválido (zero ou negativo)")
    void testSimulateLoanInvalidMonths() {
        assertThrows(IllegalArgumentException.class, () ->
                Loan.simulateLoan(loanAmount, birthDate, 0, scenario, calculationType));
        assertThrows(IllegalArgumentException.class, () ->
                Loan.simulateLoan(loanAmount, birthDate, -12, scenario, calculationType));
    }

    @Test
    @DisplayName("Deve simular um empréstimo corretamente e calcular os valores exatos")
    void testSimulateLoanIntegration() {
        Loan result = Loan.simulateLoan(loanAmount, birthDate, months, scenario, calculationType);

        BigDecimal expectedMonthlyInstallment = BigDecimal.valueOf(856.07);
        BigDecimal expectedTotalAmount = expectedMonthlyInstallment.multiply(BigDecimal.valueOf(months));
        BigDecimal expectedTotalInterest = expectedTotalAmount.subtract(loanAmount);

        assertNotNull(result);
        assertEquals(expectedMonthlyInstallment, result.getMonthlyInstallment());
        assertEquals(expectedTotalAmount, result.getTotalAmount());
        assertEquals(expectedTotalInterest, result.getTotalInterest());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para data de nascimento futura")
    void testSimulateLoanFutureBirthDate() {
        LocalDate futureBirthDate = LocalDate.now().plusYears(1);

        assertThrows(IllegalArgumentException.class, () ->
                        Loan.simulateLoan(loanAmount, futureBirthDate, months, scenario, calculationType));
    }

}
