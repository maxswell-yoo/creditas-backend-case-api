package com.github.maxswellyoo.creditas.infrastructure.gateways;

import com.github.maxswellyoo.creditas.application.gateways.EmailGateway;
import com.github.maxswellyoo.creditas.domain.entity.Loan;
import com.github.maxswellyoo.creditas.infrastructure.gateways.helpers.EmailTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SendEmailGateway implements EmailGateway {
    private static final Logger log = LoggerFactory.getLogger(SendEmailGateway.class);
    private final JavaMailSender mailSender;

    public SendEmailGateway(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendLoanEmail(String to, Loan body) {
        String emailContent = EmailTemplateBuilder.buildEmailContent(body);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject("Resultado da simulação de empréstimo");
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
            log.info("Email enviado com sucesso! {}", to);
        } catch (MessagingException exception) {
            log.error("Error ao enviar email {}", exception.getMessage());
            throw new RuntimeException("Error ao enviar email", exception);
        }
    }
}
