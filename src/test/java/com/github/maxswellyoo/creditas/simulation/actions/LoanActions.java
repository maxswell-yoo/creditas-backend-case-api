package com.github.maxswellyoo.creditas.simulation.actions;

import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class LoanActions {
    public static ChainBuilder simulateLoan() {
        String requestBody = "{ \"loanAmount\": 10000, \"birthDate\": \"11/02/1997\", \"months\": 12 }";

        return exec(
                http("Simular Empréstimo")
                        .post("/simulate-loan")
                        .body(StringBody(requestBody)).asJson()
                        .check(status().is(201))
        );
    }

//    // Ação para buscar o histórico de simulações via GET
//    public static ChainBuilder getLoanHistory() {
//        return exec(
//                http("Buscar Histórico de Simulações")
//                        .get("/loan-history")
//                        .check(status().is(200))
//                        .check(jsonPath("$").exists())  // Verifica que alguma estrutura JSON é retornada
//        );
//    }
}
