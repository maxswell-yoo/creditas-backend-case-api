package com.github.maxswellyoo.creditas.integration.infrastructure.persistence.repository;

import com.github.maxswellyoo.creditas.infrastructure.persistence.repository.LoanRepository;
import com.github.maxswellyoo.creditas.infrastructure.persistence.schema.LoanEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    private LoanEntity validLoan;

    @BeforeEach
    void setUp() {
        validLoan = new LoanEntity();
        validLoan.setLoanAmount(BigDecimal.valueOf(10000));
        validLoan.setBirthDate(LocalDate.of(2004, 2, 11));
        validLoan.setMonths(12);
        validLoan.setMonthlyInstallment(BigDecimal.valueOf(856.07));
        validLoan.setTotalAmount(BigDecimal.valueOf(10272.84));
        validLoan.setTotalInterest(BigDecimal.valueOf(272.84));
    }

    @Test
    @DisplayName("Deve salvar um LoanEntity corretamente")
    void testSaveLoanEntity() {
        LoanEntity savedLoan = loanRepository.save(validLoan);
        assertNotNull(savedLoan.getId());
        assertEquals(BigDecimal.valueOf(10000), savedLoan.getLoanAmount());
        assertEquals(LocalDate.of(2004, 2, 11), savedLoan.getBirthDate());
        assertEquals(BigDecimal.valueOf(856.07), savedLoan.getMonthlyInstallment());
        assertEquals(BigDecimal.valueOf(10272.84), savedLoan.getTotalAmount());
        assertEquals(BigDecimal.valueOf(272.84), savedLoan.getTotalInterest());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar um LoanEntity com loanAmount nulo")
    void testSaveLoanEntityInvalidLoanAmount() {
        LoanEntity invalidLoan = new LoanEntity();
        assertThrows(DataIntegrityViolationException.class, () -> loanRepository.saveAndFlush(invalidLoan));
    }
}
