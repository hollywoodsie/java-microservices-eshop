# Use a base image with Amazon Corretto 21 JDK installed
FROM amazoncorretto:21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the project's build configuration files
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

# Copy the Gradle wrapper
COPY gradle/wrapper ./gradle/wrapper/

# Copy the entire source code
COPY src ./src

# Build the project
RUN ./gradlew build

# Start a new stage for the final image
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged Spring Boot application JAR file from the builder stage
COPY --from=builder /app/build/libs/user-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Define the command to run your Spring Boot application when the container starts
CMD ["java", "-jar", "app.jar"]