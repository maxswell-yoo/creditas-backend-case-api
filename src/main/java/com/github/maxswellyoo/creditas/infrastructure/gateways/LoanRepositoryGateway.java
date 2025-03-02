package com.github.maxswellyoo.creditas.infrastructure.gateways;

import com.github.maxswellyoo.creditas.application.gateways.LoanGateway;
import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.infrastructure.gateways.mapper.LoanEntityMapper;
import com.github.maxswellyoo.creditas.infrastructure.persistence.repository.LoanRepository;
import com.github.maxswellyoo.creditas.infrastructure.persistence.schema.LoanEntity;

public class LoanRepositoryGateway implements LoanGateway {
    private final LoanRepository loanRepository;
    private final LoanEntityMapper loanEntityMapper;

    public LoanRepositoryGateway(LoanRepository loanRepository, LoanEntityMapper loanEntityMapper) {
        this.loanRepository = loanRepository;
        this.loanEntityMapper = loanEntityMapper;
    }

    @Override
    public Loan saveSimulatedLoan(Loan simulatedLoan) {
        LoanEntity loanEntity = loanEntityMapper.toEntity(simulatedLoan);
        LoanEntity savedLoanEntity = loanRepository.save(loanEntity);
        return loanEntityMapper.toDomainObject(savedLoanEntity);
    }
}
