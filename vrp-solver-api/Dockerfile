FROM maven:3.9.5-eclipse-temurin-17 AS build-stage

WORKDIR /vrp-solver-api

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /vrp-solver-api

COPY --from=build-stage /vrp-solver-api/target/*.jar /vrp-solver-api/vrp-solver-api.jar

CMD [ "java", "-jar", "vrp-solver-api.jar" ]
