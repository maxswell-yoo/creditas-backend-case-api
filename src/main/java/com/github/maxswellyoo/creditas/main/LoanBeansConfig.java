package com.github.maxswellyoo.creditas.main;

import com.github.maxswellyoo.creditas.application.gateways.EmailGateway;
import com.github.maxswellyoo.creditas.application.gateways.LoanGateway;
import com.github.maxswellyoo.creditas.application.usecases.SimulateLoanUseCase;
import com.github.maxswellyoo.creditas.infrastructure.controllers.mapper.LoanDTOMapper;
import com.github.maxswellyoo.creditas.infrastructure.gateways.LoanRepositoryGateway;
import com.github.maxswellyoo.creditas.infrastructure.gateways.SendEmailGateway;
import com.github.maxswellyoo.creditas.infrastructure.gateways.mapper.LoanEntityMapper;
import com.github.maxswellyoo.creditas.infrastructure.persistence.repository.LoanRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class LoanBeansConfig {
    @Bean
    SimulateLoanUseCase simulateLoanUseCase(LoanGateway loanGateway, EmailGateway emailGateway) {
        return new SimulateLoanUseCase(loanGateway, emailGateway);
    }

    @Bean
     EmailGateway sendEmailGateway(JavaMailSender javaMailSender) {
        return new SendEmailGateway(javaMailSender);
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
