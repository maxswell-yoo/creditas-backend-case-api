package com.github.maxswellyoo.creditas.infrastructure.controllers;

import com.github.maxswellyoo.creditas.application.usecases.SimulateLoanUseCase;
import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.infrastructure.controllers.dto.SimulateLoanRequest;
import com.github.maxswellyoo.creditas.infrastructure.controllers.dto.SimulateLoanResponse;
import com.github.maxswellyoo.creditas.infrastructure.controllers.mapper.LoanDTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("simulate-loan")
public class LoanController {
    private final SimulateLoanUseCase simulateLoanUseCase;
    private final LoanDTOMapper loanDTOMapper;

    public LoanController(SimulateLoanUseCase simulateLoanUseCase, LoanDTOMapper loanDTOMapper) {
        this.simulateLoanUseCase = simulateLoanUseCase;
        this.loanDTOMapper = loanDTOMapper;
    }

    @PostMapping
    public ResponseEntity<SimulateLoanResponse> simulateLoan(@RequestBody SimulateLoanRequest request) {
        Loan simulatedLoan = simulateLoanUseCase.simulateLoan(request.loanAmount(), request.birthDate(), request.months());
        SimulateLoanResponse loanResponse = loanDTOMapper.toResponse(simulatedLoan);
        return new ResponseEntity<SimulateLoanResponse>(loanResponse, HttpStatus.CREATED);
    }
}
