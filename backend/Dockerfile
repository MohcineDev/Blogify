# Use an official Maven image with Java 17
FROM maven:3.9.9-eclipse-temurin-17

# Set the working directory inside the container
WORKDIR /app

# Copy everything from the backend folder
COPY . .

# Expose port 8080
EXPOSE 8080

# Default command (build and run)
CMD ["./mvnw", "spring-boot:run"]
