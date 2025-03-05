package com.github.maxswellyoo.creditas.unit.infrastructure.gateways;

import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.infrastructure.gateways.LoanRepositoryGateway;
import com.github.maxswellyoo.creditas.infrastructure.gateways.mapper.LoanEntityMapper;
import com.github.maxswellyoo.creditas.infrastructure.persistence.repository.LoanRepository;
import com.github.maxswellyoo.creditas.infrastructure.persistence.schema.LoanEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanRepositoryGatewayTest {
    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanEntityMapper loanEntityMapper;

    @InjectMocks
    private LoanRepositoryGateway loanRepositoryGateway;

    private LoanEntity loanEntity;
    private LoanEntity loanEntitySalvo;
    private Loan loanSimulado;

    @BeforeEach
    void setUp() {
        BigDecimal loanAmount = BigDecimal.valueOf(10000);
        LocalDate birthDate = LocalDate.of(2004, 11, 2);
        int months = 12;
        String email = "test@test.com";

        loanEntity = new LoanEntity();
        loanEntity.setId(1L);
        loanEntity.setLoanAmount(loanAmount);
        loanEntity.setBirthDate(birthDate);
        loanEntity.setMonths(months);

        loanEntitySalvo = new LoanEntity();
        loanEntitySalvo.setId(1L);
        loanEntitySalvo.setLoanAmount(loanAmount);
        loanEntitySalvo.setBirthDate(birthDate);
        loanEntitySalvo.setMonths(months);

        loanSimulado = new Loan(
                loanAmount,
                birthDate,
                months,
                BigDecimal.valueOf(856.07),
                BigDecimal.valueOf(10272.84),
                BigDecimal.valueOf(272.84),
                email
        );
    }

    @Test
    @DisplayName("Deve converter, salvar e retornar o empréstimo corretamente")
    void shouldSaveAndConvertLoanCorrectly() {
        when(loanEntityMapper.toEntity(loanSimulado)).thenReturn(loanEntity);
        when(loanRepository.save(loanEntity)).thenReturn(loanEntitySalvo);
        when(loanEntityMapper.toDomainObject(loanEntitySalvo)).thenReturn(loanSimulado);

        Loan resultado = loanRepositoryGateway.saveSimulatedLoan(loanSimulado);

        assertNotNull(resultado);
        assertEquals(loanSimulado, resultado);

        verify(loanEntityMapper, times(1)).toEntity(loanSimulado);
        verify(loanRepository, times(1)).save(loanEntity);
        verify(loanEntityMapper, times(1)).toDomainObject(loanEntitySalvo);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para empréstimo nulo")
    void shouldThrowExceptionForNullLoan() {
        when(loanEntityMapper.toEntity(null)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> loanRepositoryGateway.saveSimulatedLoan(null));
    }
}
