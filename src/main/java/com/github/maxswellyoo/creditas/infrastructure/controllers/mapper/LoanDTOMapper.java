package com.github.maxswellyoo.creditas.infrastructure.controllers.mapper;

import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.infrastructure.controllers.dto.SimulateLoanResponse;

public class LoanDTOMapper {
    public SimulateLoanResponse toResponse(Loan loan) {
        return new SimulateLoanResponse(loan.getTotalAmount(), loan.getMonthlyInstallment(), loan.getMonths(), loan.getTotalInterest());
    }
}
