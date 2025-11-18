# 1. Construir (Maven incluido)
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# 2. Ejecutar (JRE 17)
FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]