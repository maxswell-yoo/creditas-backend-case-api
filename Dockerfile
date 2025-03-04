FROM maven:3.9-eclipse-temurin-21 AS base
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline

FROM base As gatling
WORKDIR /app
COPY pom.xml .
COPY src src/

FROM base AS build
WORKDIR /app
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21.0.4_7-jdk AS final
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
