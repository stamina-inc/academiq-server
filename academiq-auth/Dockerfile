# Build stage
FROM maven:3.9.11 AS build

# Set the working directory inside the build container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .

# Download the project dependencies
RUN mvn dependency:go-offline

# Copy the source code to the container
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Final stage
FROM openjdk:21

# Copy the built Spring Boot jar file into the container
COPY target/academiq-auth-*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]