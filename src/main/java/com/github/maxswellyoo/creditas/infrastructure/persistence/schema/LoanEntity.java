package com.github.maxswellyoo.creditas.infrastructure.persistence.schema;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "LOAN")
@Entity
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private BigDecimal loanAmount;
    @Column(nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private int months;
    @Column(nullable = false)
    private BigDecimal monthlyInstallment;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    @Column(nullable = false)
    private BigDecimal totalInterest;

    public LoanEntity(
            BigDecimal loanAmount,
            LocalDate birthDate,
            int months,
            String email,
            BigDecimal monthlyInstallment,
            BigDecimal totalAmount,
            BigDecimal totalInterest) {
        this.loanAmount = loanAmount;
        this.birthDate = birthDate;
        this.months = months;
        this.email = email;
        this.monthlyInstallment = monthlyInstallment;
        this.totalAmount = totalAmount;
        this.totalInterest = totalInterest;
    }

    public LoanEntity() {

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public int getMonths() {
        return months;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }


}
