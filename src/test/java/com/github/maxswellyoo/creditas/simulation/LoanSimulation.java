package com.github.maxswellyoo.creditas.simulation;

import com.github.maxswellyoo.creditas.simulation.config.HttpConfig;
import com.github.maxswellyoo.creditas.simulation.scenarios.LoanScenarios;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.OpenInjectionStep.nothingFor;

public class LoanSimulation extends Simulation {

    public LoanSimulation() {
        setUp(
                LoanScenarios.SIMULATION_SCENARIO.injectOpen(
                        constantUsersPerSec(2).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(15)).randomized(),
                        rampUsersPerSec(10).to(500).during(Duration.ofMinutes(1)),
                        nothingFor(Duration.ofSeconds(10)),
                        rampUsersPerSec(10).to(500).during(Duration.ofMinutes(1))
                )
        ).protocols(HttpConfig.HTTP_PROTOCOL);
    }
}

