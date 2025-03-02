CREATE TABLE LOAN
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_amount         DECIMAL(19, 2) NOT NULL,
    birth_date          DATE           NOT NULL,
    months              INT            NOT NULL,
    monthly_installment DECIMAL(19, 2) NOT NULL,
    total_amount        DECIMAL(19, 2) NOT NULL,
    total_interest      DECIMAL(19, 2) NOT NULL
);