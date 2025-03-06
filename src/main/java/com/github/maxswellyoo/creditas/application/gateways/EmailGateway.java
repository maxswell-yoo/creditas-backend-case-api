package com.github.maxswellyoo.creditas.application.gateways;

import com.github.maxswellyoo.creditas.domain.entity.Loan;

public interface EmailGateway {
    void sendLoanEmail(String to, Loan body);
}
