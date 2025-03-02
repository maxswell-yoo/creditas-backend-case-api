package com.github.maxswellyoo.creditas.simulation.scenarios;

import com.github.maxswellyoo.creditas.simulation.actions.LoanActions;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class LoanScenarios {
    public static final ScenarioBuilder SIMULATION_SCENARIO = scenario("Loan Simulation Scenario")
            .exec(LoanActions.simulateLoan());
}
