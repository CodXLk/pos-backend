FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built jar file
COPY target/pos-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
