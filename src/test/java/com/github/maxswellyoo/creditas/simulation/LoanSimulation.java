package com.github.maxswellyoo.creditas.simulation;

import com.github.maxswellyoo.creditas.simulation.config.HttpConfig;
import com.github.maxswellyoo.creditas.simulation.scenarios.LoanScenarios;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class LoanSimulation extends Simulation {

    public LoanSimulation() {
        setUp(
                LoanScenarios.SIMULATION_SCENARIO.injectOpen(
                        constantUsersPerSec(10000).during(Duration.ofSeconds(30))
                )
        ).protocols(HttpConfig.HTTP_PROTOCOL);
    }
}

