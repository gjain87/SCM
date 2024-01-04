# Base image for Java 8
FROM openjdk:8-jdk-alpine

# Create a working directory
WORKDIR /

# Copy the compiled JAR file to the working directory
COPY target/SCM-0.0.1-SNAPSHOT.jar /SCM.jar

# Expose the application port
EXPOSE 8080

# Entrypoint to start the application
ENTRYPOINT ["java", "SCM-0.0.1-SNAPSHOT-jar", "/SCM.jar"]
