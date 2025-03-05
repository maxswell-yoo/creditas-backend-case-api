package com.github.maxswellyoo.creditas.application.usecases;

import com.github.maxswellyoo.creditas.application.gateways.LoanGateway;
import com.github.maxswellyoo.creditas.application.gateways.EmailGateway;
import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.domain.enums.CalculationType;
import com.github.maxswellyoo.creditas.domain.enums.InterestRateScenario;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SimulateLoanUseCase {
    private final LoanGateway loanGateway;
    private final EmailGateway emailGateway;

    public SimulateLoanUseCase(LoanGateway loanGateway, EmailGateway emailGateway) {
        this.loanGateway = loanGateway;
        this.emailGateway = emailGateway;
    }

    public Loan simulateLoan(BigDecimal loanAmount, LocalDate birthDate, int months, String email) {
        Loan simulatedLoan = Loan.simulateLoan(
                loanAmount,
                birthDate,
                months,
                email,
                InterestRateScenario.FIXED,
                CalculationType.FIXED
        );
        Loan savedSimulatedLoan = loanGateway.saveSimulatedLoan(simulatedLoan);
        emailGateway.sendLoanEmail(email, savedSimulatedLoan);
        return savedSimulatedLoan;
    }
}
