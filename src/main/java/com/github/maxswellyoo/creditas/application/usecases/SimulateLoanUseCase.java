package com.github.maxswellyoo.creditas.application.usecases;

import com.github.maxswellyoo.creditas.application.gateways.LoanGateway;
import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SimulateLoanUseCase {
    private final LoanGateway loanGateway;

    public SimulateLoanUseCase(LoanGateway loanGateway) {
        this.loanGateway = loanGateway;
    }

    public Loan simulateFixedLoan(BigDecimal principal, LocalDate birthDate, int months) {
        Loan simulatedLoan = Loan.simulateLoan(
                principal,
                birthDate,
                months,
                InterestRateScenario.FIXED,
                CalculationType.FIXED
        );
        return loanGateway.saveSimulatedLoan(simulatedLoan);
    }
}
