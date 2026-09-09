# ================================
# Stage 1: Build Spring Boot App
# ================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy Maven wrapper
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Give Maven wrapper execute permission
RUN chmod +x mvnw

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests


# ================================
# Stage 2: Run Spring Boot App
# ================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy generated JAR
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Render uses PORT at runtime
EXPOSE 10000

# Start application
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseSerialGC", "-jar", "app.jar"]