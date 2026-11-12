FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S pricing && adduser -S pricing -G pricing
USER pricing:pricing
WORKDIR /app
COPY --from=build /workspace/target/pricing-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
