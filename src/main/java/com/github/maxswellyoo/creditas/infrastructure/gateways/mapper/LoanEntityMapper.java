package com.github.maxswellyoo.creditas.infrastructure.gateways.mapper;

import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.infrastructure.persistence.schema.LoanEntity;

public class LoanEntityMapper {
    public LoanEntity toEntity(Loan loanDomainObject) {
        return new LoanEntity(
                loanDomainObject.getLoanAmount(),
                loanDomainObject.getBirthDate(),
                loanDomainObject.getMonths(),
                loanDomainObject.getMonthlyInstallment(),
                loanDomainObject.getTotalAmount(),
                loanDomainObject.getTotalInterest()
        );
    }

    public Loan toDomainObject(LoanEntity loanEntity) {
        return new Loan(
          loanEntity.getLoanAmount(),
          loanEntity.getBirthDate(),
          loanEntity.getMonths(),
          loanEntity.getMonthlyInstallment(),
          loanEntity.getTotalAmount(),
          loanEntity.getTotalInterest()
        );
    }
}
