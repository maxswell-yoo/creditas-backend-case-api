package com.github.maxswellyoo.creditas.infrastructure.gateways.mapper;

import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.infrastructure.persistence.schema.LoanEntity;

public class LoanEntityMapper {
    public LoanEntity toEntity(Loan loanDomainObject) {
        if (loanDomainObject == null) {
            throw new IllegalArgumentException("O objeto Loan não pode ser nulo");
        }
        return new LoanEntity(
                loanDomainObject.getLoanAmount(),
                loanDomainObject.getBirthDate(),
                loanDomainObject.getMonths(),
                loanDomainObject.getEmail(),
                loanDomainObject.getMonthlyInstallment(),
                loanDomainObject.getTotalAmount(),
                loanDomainObject.getTotalInterest(),
                loanDomainObject.getCurrency()
        );
    }

    public Loan toDomainObject(LoanEntity loanEntity) {
        if (loanEntity == null) {
            throw new IllegalArgumentException("O objeto LoanEntity não pode ser nulo");
        }

        return new Loan(
                loanEntity.getLoanAmount(),
                loanEntity.getBirthDate(),
                loanEntity.getMonths(),
                loanEntity.getMonthlyInstallment(),
                loanEntity.getTotalAmount(),
                loanEntity.getTotalInterest(),
                loanEntity.getEmail(),
                loanEntity.getCurrency()
        );
    }
}
