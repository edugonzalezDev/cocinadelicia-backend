# --------- Stage 1: build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp package -DskipTests

# --------- Stage 2: run ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Render te pone PORT, pero por las dudas:
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=dev

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]
