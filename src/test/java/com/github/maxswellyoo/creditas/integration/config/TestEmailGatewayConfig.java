package com.github.maxswellyoo.creditas.integration.config;

import com.github.maxswellyoo.creditas.application.gateways.EmailGateway;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableAutoConfiguration(exclude = {MailSenderAutoConfiguration.class})
public class TestEmailGatewayConfig {

    @Bean
    @Primary
    public EmailGateway testEmailGateway() {
        return (to, body) -> System.out.println("Simulação de envio de e-mail para: " + to);
    }
}