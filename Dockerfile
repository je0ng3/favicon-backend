FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM amazoncorretto:17.0.12
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]