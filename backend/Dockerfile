# 1. Construir (usa JDK 17 para compilar)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY mvnw .mvn .mvn pom.xml ./
RUN chmod +x mvnw

# Descargar dependencias (rápido la próxima vez)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests -B

# 2. Ejecutar (usa JRE 17, más pequeño)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia cualquier JAR *-SNAPSHOT.jar
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]