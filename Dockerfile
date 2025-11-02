# ==== STAGE 1: build ====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copiamos pom y resolvemos deps primero (cache)
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# ahora copiamos el código
COPY src ./src

# build (si querés saltar tests en render: -DskipTests)
RUN mvn -B -ntp package -DskipTests

# ==== STAGE 2: runtime ====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# crea un usuario no root (opcional pero recomendado)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# copia el jar
COPY --from=build /app/target/*.jar app.jar

# puerto por el que escucha la app
EXPOSE 8080

# Spring leerá SPRING_PROFILES_ACTIVE del entorno
ENTRYPOINT ["java","-jar","/app/app.jar"]
