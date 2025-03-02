package com.github.maxswellyoo.creditas.infrastructure.persistence.repository;

import com.github.maxswellyoo.creditas.infrastructure.persistence.schema.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
}
