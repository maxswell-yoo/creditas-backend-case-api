package com.github.maxswellyoo.creditas.simulation.config;

import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

public class HttpConfig {
    private static final String BASE_URL = System.getenv("URL_SIMULATION") != null ?
            System.getenv("URL_SIMULATION") :
            "http://localhost:8080";

    public static final HttpProtocolBuilder HTTP_PROTOCOL = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");
}
