package com.github.maxswellyoo.creditas.application.usecases;

import com.github.maxswellyoo.creditas.application.gateways.EmailGateway;
import com.github.maxswellyoo.creditas.application.gateways.LoanGateway;
import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.enums.Currency;
import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.github.maxswellyoo.creditas.domain.enums.Currency.BRL;
import static com.github.maxswellyoo.creditas.domain.enums.CurrencyConversionType.DEFAULT;
import static com.github.maxswellyoo.creditas.domain.service.CurrencyConversionService.convertCurrency;

public class SimulateLoanUseCase {
    private final LoanGateway loanGateway;
    private final EmailGateway emailGateway;

    public SimulateLoanUseCase(LoanGateway loanGateway, EmailGateway emailGateway) {
        this.loanGateway = loanGateway;
        this.emailGateway = emailGateway;
    }

    public Loan simulateLoan(BigDecimal loanAmount, LocalDate birthDate, int months, String email, Currency fromCurrency) {
        BigDecimal loanAmountConverted = convertCurrency(loanAmount, fromCurrency, BRL, DEFAULT);
        Loan simulatedLoan = Loan.simulateLoan(
                loanAmountConverted,
                birthDate,
                months,
                email,
                InterestRateScenario.FIXED,
                CalculationType.FIXED,
                fromCurrency
        );
        Loan savedSimulatedLoan = loanGateway.saveSimulatedLoan(simulatedLoan);
        emailGateway.sendLoanEmail(email, savedSimulatedLoan);
        return savedSimulatedLoan;
    }
}
