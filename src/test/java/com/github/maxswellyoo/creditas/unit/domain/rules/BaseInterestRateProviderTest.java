package com.github.maxswellyoo.creditas.unit.domain.rules;

import static org.junit.jupiter.api.Assertions.*;

import com.github.maxswellyoo.creditas.domain.rules.BaseInterestRateProvider;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BaseInterestRateProviderTest {
    @Test
    @DisplayName("Deve retornar 0.05 para idade entre 0 e 25")
    void testGetBaseInterestRateForYoung() {
        LocalDate birthDate = LocalDate.now().minusYears(20);
        BigDecimal rate = BaseInterestRateProvider.getBaseInterestRate(birthDate);
        assertEquals(BigDecimal.valueOf(0.05), rate);
    }

    @Test
    @DisplayName("Deve retornar 0.03 para idade entre 26 e 40")
    void testGetBaseInterestRateForAdult() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        BigDecimal rate = BaseInterestRateProvider.getBaseInterestRate(birthDate);
        assertEquals(BigDecimal.valueOf(0.03), rate);
    }

    @Test
    @DisplayName("Deve retornar 0.02 para idade entre 41 e 60")
    void testGetBaseInterestRateForMiddleAged() {
        LocalDate birthDate = LocalDate.now().minusYears(50);
        BigDecimal rate = BaseInterestRateProvider.getBaseInterestRate(birthDate);
        assertEquals(BigDecimal.valueOf(0.02), rate);
    }

    @Test
    @DisplayName("Deve retornar 0.04 para idade acima de 60")
    void testGetBaseInterestRateForSenior() {
        LocalDate birthDate = LocalDate.now().minusYears(70);
        BigDecimal rate = BaseInterestRateProvider.getBaseInterestRate(birthDate);
        assertEquals(BigDecimal.valueOf(0.04), rate);
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException para data de nascimento futura")
    void testGetBaseInterestRateForFutureBirthDate() {
        LocalDate futureBirthDate = LocalDate.now().plusYears(1);
        int age = Period.between(futureBirthDate, LocalDate.now()).getYears();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                BaseInterestRateProvider.getBaseInterestRate(futureBirthDate));
        assertEquals("Nenhuma regra encontrada para a idade: " + age, exception.getMessage());
    }
}
