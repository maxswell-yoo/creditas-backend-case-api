package com.github.maxswellyoo.creditas.main;

import com.github.maxswellyoo.creditas.application.gateways.LoanGateway;
import com.github.maxswellyoo.creditas.application.usecases.SimulateLoanUseCase;
import com.github.maxswellyoo.creditas.infrastructure.controllers.mapper.LoanDTOMapper;
import com.github.maxswellyoo.creditas.infrastructure.gateways.LoanRepositoryGateway;
import com.github.maxswellyoo.creditas.infrastructure.gateways.mapper.LoanEntityMapper;
import com.github.maxswellyoo.creditas.infrastructure.persistence.repository.LoanRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanBeansConfig {
    @Bean
    SimulateLoanUseCase simulateLoanUseCase(LoanGateway loanGateway) {
        return new SimulateLoanUseCase(loanGateway);
    }

    @Bean
    LoanGateway loanGateway(LoanRepository loanRepository, LoanEntityMapper loanEntityMapper) {
        return new LoanRepositoryGateway(loanRepository, loanEntityMapper);
    }

    @Bean
    LoanEntityMapper loanEntityMapper() {
        return new LoanEntityMapper();
    }

    @Bean
    LoanDTOMapper loanDTOMapper() {
        return new LoanDTOMapper();
    }
}
