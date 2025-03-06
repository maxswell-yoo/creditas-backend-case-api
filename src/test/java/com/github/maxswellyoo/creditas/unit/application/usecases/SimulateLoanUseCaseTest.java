package com.github.maxswellyoo.creditas.unit.application.usecases;

import static com.github.maxswellyoo.creditas.domain.enums.Currency.BRL;
import static com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.maxswellyoo.creditas.application.gateways.EmailGateway;
import com.github.maxswellyoo.creditas.application.gateways.LoanGateway;
import com.github.maxswellyoo.creditas.application.usecases.SimulateLoanUseCase;
import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.enums.Currency;
import com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType;
import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;
import com.github.maxswellyoo.creditas.domain.service.CurrencyConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class SimulateLoanUseCaseTest {
    @Mock
    private LoanGateway loanGateway;
    @Mock
    private EmailGateway emailGateway;
    @InjectMocks
    private SimulateLoanUseCase simulateLoanUseCase;

    private Loan simulatedLoan;
    private BigDecimal loanAmount;
    private LocalDate birthDate;
    private int months;
    private String email;
    private Currency currency = BRL;

    @BeforeEach
    void setUp() {
        loanAmount = BigDecimal.valueOf(4000);
        birthDate = LocalDate.of(1997, 2, 11);
        months = 14;
        email = "test@gmail.com";

        simulatedLoan = new Loan(
                loanAmount,
                birthDate,
                months,
                BigDecimal.valueOf(727.25),
                BigDecimal.valueOf(10188.50),
                BigDecimal.valueOf(188.50),
                email,
                currency
        );
        when(loanGateway.saveSimulatedLoan(any(Loan.class))).thenReturn(simulatedLoan);
    }

    @Test
    @DisplayName("Deve simular e salvar um loan corretamente")
    void simulateLoan() {
        try (MockedStatic<Loan> loanMockedStatic = mockStatic(Loan.class);
             MockedStatic<CurrencyConversionService> conversionServiceMockedStatic = mockStatic(CurrencyConversionService.class)) {
            loanMockedStatic.when(() -> Loan.simulateLoan(
                            loanAmount,
                            birthDate,
                            months,
                            email,
                            InterestRateScenario.FIXED,
                            CalculationType.FIXED,
                            currency))
                    .thenReturn(simulatedLoan);
            conversionServiceMockedStatic.when(() -> CurrencyConversionService.convertCurrency(
                    loanAmount,
                    currency,
                    BRL,
                    DEFAULT)
            ).thenReturn(loanAmount);

            Loan result = simulateLoanUseCase.simulateLoan(loanAmount, birthDate, months, email, currency);

            assertNotNull(result);
            assertEquals(simulatedLoan, result);
            verify(loanGateway, times(1)).saveSimulatedLoan(any(Loan.class));
            loanMockedStatic.verify(() -> Loan.simulateLoan(
                    loanAmount,
                    birthDate,
                    months,
                    email,
                    InterestRateScenario.FIXED,
                    CalculationType.FIXED,
                    currency), times(1));
            loanMockedStatic.verifyNoMoreInteractions();
            conversionServiceMockedStatic.verify(() -> CurrencyConversionService.convertCurrency(
                    loanAmount,
                    currency,
                    BRL,
                    DEFAULT
            ), times(1));
            conversionServiceMockedStatic.verifyNoMoreInteractions();
        }
    }
}
