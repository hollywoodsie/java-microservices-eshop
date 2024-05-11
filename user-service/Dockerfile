# Use a base image with Amazon Corretto 21 JDK installed
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged Spring Boot application JAR file into the container at /app
COPY build/libs/user-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that your Spring Boot application runs on
EXPOSE 8082

# Define the command to run your Spring Boot application when the container starts
CMD ["java", "-jar", "app.jar"]