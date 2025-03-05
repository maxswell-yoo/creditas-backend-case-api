package com.github.maxswellyoo.creditas.infrastructure.gateways.helpers;

import com.github.maxswellyoo.creditas.domain.entity.Loan;

public class EmailTemplateBuilder {
    private static final String TEMPLATE = """
            <html>
              <head>
                <style>
                  body { font-family: Arial, sans-serif; }
                  h2 { color: #2E86C1; }
                  p { font-size: 14px; }
                </style>
              </head>
              <body>
                <h2>Detalhes da Simulação de Empréstimo</h2>
                <p><strong>Valor do Empréstimo:</strong> R$ %s</p>
                <p><strong>Número de Parcelas:</strong> %d</p>
                <p><strong>Parcela Mensal:</strong> R$ %s</p>
                <p><strong>Total a Pagar:</strong> R$ %s</p>
                <p><strong>Total de Juros:</strong> R$ %s</p>
              </body>
            </html>
            """;

    public static String buildEmailContent(Loan loan) {
        return String.format(
                TEMPLATE,
                loan.getLoanAmount().toPlainString(),
                loan.getMonths(),
                loan.getMonthlyInstallment().toPlainString(),
                loan.getTotalAmount().toPlainString(),
                loan.getTotalInterest().toPlainString()
        );
    }
}
