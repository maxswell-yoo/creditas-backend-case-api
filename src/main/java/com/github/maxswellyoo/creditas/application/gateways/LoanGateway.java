package com.github.maxswellyoo.creditas.application.gateways;

import com.github.maxswellyoo.creditas.domain.entity.Loan;

public interface LoanGateway {
    Loan saveSimulatedLoan(Loan simulatedLoan);
}
